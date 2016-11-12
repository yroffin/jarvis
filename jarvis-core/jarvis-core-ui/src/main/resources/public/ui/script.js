/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/**
 * Declare app level module which depends on filters, and services
 */

angular.module('JarvisApp', [
     'ngMaterial',
     'ngMdIcons',
     'ngSanitize',
     'restangular',
     'angular-websocket',
     'ui.router',
     'ui.router.router',
     'pascalprecht.translate',
     'ngCookies',
     'hljs',
     'ui.tree',
     'JarvisApp.websocket',
     'JarvisApp.config',
     'JarvisApp.theme',
     'JarvisApp.routes',
     /**
      * services
      */
     'JarvisApp.services',
     'JarvisApp.services.filter',
     'JarvisApp.services.generic',
     'JarvisApp.services.scope',
     'JarvisApp.directives.files',
     'JarvisApp.directives.widgets',
     /**
      * directives
      */
     'jarvis.directives.navigator',
     'jarvis.directives.command',
     'jarvis.directives.cron',
     'jarvis.directives.scenario',
     'jarvis.directives.trigger',
     'jarvis.directives.configuration',
     'jarvis.directives.notification',
     'jarvis.directives.connector',
     'jarvis.directives.block',
     'jarvis.directives.device',
     'jarvis.directives.event',
     'jarvis.directives.home',
     'jarvis.directives.plugin',
     'jarvis.directives.property',
     'jarvis.directives.snapshot',
     'jarvis.directives.view',
]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Services */

var jarvisServicesUrl = '/services';
var jarvisApiUrl = '/api';
var myAppServices = angular.module('JarvisApp.services', [ 'ngResource' ]);

/**
 * toastService
 */
myAppServices.factory('toastService', [ '$log', '$mdToast', function($log, $mdToast) {
  var $log =  angular.injector(['ng']).get('$log');
  $log.info('toastService', $mdToast);

  var toastServiceInstance;
  toastServiceInstance = {
		toastPosition: {
		          bottom: true,
		          top: false,
		          left: false,
		          right: true
		},
		getToastPosition: function() {
		     return Object.keys(toastServiceInstance.toastPosition)
		         .filter(function(pos) { return toastServiceInstance.toastPosition[pos]; })
		         .join(' ');
		},
        failure: function(failure) {
        	$log.error(failure);
            $mdToast.show(
                    $mdToast.simple()
                        .content(failure)
                        .position(toastServiceInstance.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
        },
        info: function(message) {
        	$log.info(message,toastServiceInstance.getToastPosition());
            $mdToast.show(
                    $mdToast.simple()
                        .position(toastServiceInstance.getToastPosition())
                        .textContent(message)
                        .hideDelay(3000)
                        .capsule(true)
                );
        }
  }
  return toastServiceInstance;
}]);

/**
 * $store service
 */
myAppServices.factory('$store', [ '$log', '$rootScope', function($log, $rootScope) {
  $log.info("$store");

  /**
   * collection the main store var
   * all pushed data ae stored in this object
   */
  var collection = {};
  
  var methods = {
	        collection: collection,
	        /**
	         * pus data in store, data must be a plain object
	         */
	        push: function(classname, instance, data) {
	        	/**
	        	 * create classname map if does not exist
	        	 */
	        	if(collection[classname] === undefined) {
	        		collection[classname] = {};
	        	}
	        	/**
	        	 * store new data
	        	 */
	        	$log.info("$store", classname, instance, data);
	            collection[classname][instance] = data;
	        },
	        /**
	         * retrieve value
	         */
	    	get: function(classname, instance, def) {
	    		if(collection == undefined) return def;
	    		if(collection[classname] == undefined) return def;
	    		if(collection[classname][instance] == undefined) return def;
	    		return collection[classname][instance];
	    	}	        
	      };

  return methods;
}]);

/**
 * crontabResourceService
 */
myAppServices.factory('oauth2ResourceService', 
		[
		 '$rootScope',
		 '$log',
		 '$window',
		 '$mdDialog',
		 '$location',
		 'Restangular',
		 function(
				 $rootScope,
				 $log,
				 $window,
				 $mdDialog,
				 $location,
				 Restangular
				 ) {
		  var $log =  angular.injector(['ng']).get('$log');
		  return {
			  	/**
			  	 * me service retrieve current user identity
			  	 */
		        me: function(token, callback, failure) {
		        	$log.info("Fix JarvisAuthToken to", token);
		        	Restangular.setDefaultHeaders ({
		        		'JarvisAuthToken' : token
		        	}); 
		        	Restangular.one('/api/profile/me').get().then(
		        		function(profile) {
			        		callback(profile);
			        	},function(errors){
			        		failure(errors);
			        	}
			        );
		        },
			  	/**
			  	 * retrieve oauth2 identity
			  	 */
		        config: function(args, callback, failure) {
		        	Restangular.one('oauth2').get(args).then(
		        		function(profile) {
			        		callback(profile);
			        	},function(errors){
			        		failure(errors);
			        	}
			        );
		        },
			  	/**
			  	 * connect
			  	 */
		        connect: function($scope, token) {
			        $log.info('connect with', token);
		        	var self = this;
		        	self.me(
		        		token,
	        			function(data) {
	        				$log.info('profile', data);
	        				$mdDialog.hide();
	        				$rootScope.profile = data;
		    	        	$scope.boot();
	        			},
	        			function(failure) {
	        				$log.warn('no profile');
	        				self.login();
	        			}
		        	);
		        },
			  	/**
			  	 * login
			  	 */
		        login: function() {
		        	$mdDialog.show({
		        	      controller: 'oauth2DialogCtrl',
		        	      templateUrl: '/ui/js/partials/dialog/oauth2Dialog.tmpl.html',
		        	      parent: angular.element(document.body),
		        	      clickOutsideToClose:false,
		        	      fullscreen: false
		        	});
		        }
		  }
		}
]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Services */

var myAppServices = angular.module('JarvisApp.services.filter', []);

/**
 * filterService
 */
myAppServices.factory('filterService', [ 'Restangular', function(Restangular) {
  var base = {
	  /**
	   * strip restangular object from context
	   */
	  plain: function(element) {
		  if(angular.isArray(element) || angular.isObject(element)) {
			  var element = Restangular.stripRestangular(element);
			  delete element.originalElement;
			  return element;
		  } else {
			  return element;
		  }
	  }
  }
  return base;
}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Services */

/**
 * blockResourceService
 */
angular.module('JarvisApp.services.generic', ['JarvisApp.services.filter'])
  .factory('genericResourceService', [ '$log', 'Restangular', 'filterService', 'toastService', function($log, Restangular, filterService, toastService) {
  var resources = {
        /**
		 * find all elements
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(path, callback, failure) {
			var handler = function(elements) {
                var results = [];
            	_.forEach(elements, function(element) {
            		results.push(filterService.plain(element));
                });
				$log.debug("[FIND]",path, results);
				if(callback) callback(results);
			}
			if(path.length == 1) {
				Restangular.all('/api/'+path[0]).getList().then(
						handler,
						function(errors){
							failure(errors);
				});
			} else {
				Restangular.all('/api/'+path[0]).all(path[1]).getList().then(
						handler,
						function(errors){
							failure(errors);
				});
			}
		},
        /**
		 * find one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		get: function(path, id, callback, failure) {
			var handler = function(toget) {
				var filtered = filterService.plain(toget);
				$log.debug("[GET]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one('/api/'+path[0], id).get().then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all('/api/'+path[0]).one(path[1], id).get().then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * delete one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(path, id, callback, failure) {
			var handler = function(todelete) {
				var filtered = filterService.plain(todelete);
				$log.debug("[DELETE]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one('/api/'+path[0], id).remove().then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all('/api/'+path[0]).one(path[1], id).remove().then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * update one element
		 * @param element, previous loaded element
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(path, element, callback, failure) {
			var handler = function(tosave) {
				var filtered = filterService.plain(tosave);
				$log.debug("[PUT]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one('/api/'+path[0], element.id).customPUT(element).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all('/api/'+path[0]).one(path[1], element.id).customPUT(element).then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * create one element
		 * @param element, element to create
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(path, element, callback, failure) {
			var handler = function(topost) {
				var filtered = filterService.plain(topost);
				$log.debug("[POST]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.all('/api/'+path[0]).post(element).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all('/api/'+path[0]).all(path[1]).post(element).then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * execute task
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		task: function(path, id, task, args, callback, failure) {
			var handler = function(totask) {
				var filtered = filterService.plain(totask);
				$log.debug("[TASK]",totask,path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.all('/api/'+path[0]).one(id).customPOST(args,'', {'task':task}).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all('/api/'+path[0]).all(path[1]).one(id).customPOST(args,'', {'task':task}).then(handler,function(errors){failure(errors);});
			}
		}
  };
  /**
   * crud operation
   */
  var crud = function(path) {
	return {
	  	/**
		 * find all elements
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(callback, failure) {
			return resources.findAll(path, callback, failure);
		},
		/**
		 * find one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		get: function(id, callback, failure) {
			return resources.get(path, id, callback, failure);
		},
		/**
		 * delete one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(id, callback, failure) {
			return resources.delete(path, id, callback, failure);
		},
		/**
		 * update one element
		 * @param element, previous loaded element
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(element, callback, failure) {
			return resources.put(path, element, callback, failure);
		},
		/**
		 * create one element
		 * @param element, element to create
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(element, callback, failure) {
			return resources.post(path, element, callback, failure);
		},
        /**
		 * execute task
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		task: function(id, task, args, callback, failure) {
			return resources.task(path, id, task, args, callback, failure);
		}
	 }
  }
  var links = {
			/**
			 * find all links
			 * @param api, api name
			 * @param path, resource path
			 * @param owner
			 * @param callback
			 * @param failure
			 */
			findAll: function(api, path, relation, id, callback, failure) {
				var handler = function(hrefs) {
					var results = [];
	            	_.forEach(hrefs, function(href) {
	            		results.push(filterService.plain(href));
	            	});
					$log.debug("[FIND/L/"+relation+"]",api, path, id, results);
	            	callback(results);
				};
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], id).all(path[0]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], id).all(path[0]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], id).all(path[0]).all(path[1]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], id).all(path[0]).all(path[1]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					}
				}
			},
	        /**
			 * put link
			 */
	        post: function(api, path, relation, owner, child, properties, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[POST/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
				if(properties != undefined) {
					properties.href = relation;
				}
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).one(path[0],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).one(path[0],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).all(path[0]).one(path[1],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).all(path[0]).one(path[1],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					}
				}
	        },
	        /**
			 * put link
			 */
	        put: function(api, path, relation, owner, child, instance, properties, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[PUT/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
	        	var p = {};
	        	if(properties === undefined) {
	        		p = {};
	        	} else {
	        		p = properties;
	        	}
				p.href = relation;
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).one(path[0],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).one(path[0],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).all(path[0]).one(path[1],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).all(path[0]).one(path[1],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					}
				}
	        },
	        /**
			 * delete link
			 */
			delete: function(api, path, relation, owner, child, instance, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[DELETE/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).one(path[0], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).one(path[0], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one('/api/'+api[0], owner).all(path[0]).one(path[1], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all('/api/'+api[0]).one(api[1], owner).all(path[0]).one(path[1], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					}
				}
			}
  };
  /**
   * crud operation on links
   */
  var crudLinks = function(api, path, relation) {
	if(relation === undefined) {
		relation = "HREF";
	}
	return {
	  	/**
		 * find all links
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(id, callback, failure) {
			return links.findAll(api, path, relation, id, callback, failure);
		},
		/**
		 * delete one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(owner, child, instance, callback, failure) {
			return links.delete(api, path, relation, owner, child, instance, callback, failure);
		},
		/**
		 * update one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(owner, child, instance, properties, callback, failure) {
			return links.put(api, path, relation, owner, child, instance, properties, callback, failure);
		},
		/**
		 * create one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(owner, child, properties, callback, failure) {
			return links.post(api, path, relation, owner, child, properties, callback, failure);
		}
	 }
  }
  /**
   * crud operation on links
   */
  var scopeCrud = {
	   	new : function(name, elements, update, service, callback) {
	        /**
	         * create this elements
	         */
	   		service.post(update, function(data) {
	                $log.debug(name + ' ' + data.name + '#' + data.id +' created');
	                elements.push(data);
	                if(callback) {
	                	callback(data);
	                }
	        }, toastService.failure);
	    },
	    remove : function(go, name, todelete, service) {
	    	service.delete(todelete.id, function(element) {
	        	toastService.info(name + ' ' + todelete.name + '#' + todelete.id + ' removed');
	        	go();
	        }, toastService.failure);
	    },
	    save : function(name, toput, service) {
	    	service.put(toput, function(element) {
	        	toastService.info(name + ' ' + toput.name + '#' + toput.id + ' updated');
	        }, toastService.failure);
	    },
	    duplicate : function(go, name, toduplicate, service) {
	    	service.post(toduplicate, function(element) {
	        	toastService.info(name + ' ' + toduplicate.name + '#' + toduplicate.id + ' duplicated');
	        	go();
	        }, toastService.failure);
	    },
	    addLink : function(id, link, properties, service, links) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.post(id, link.id, properties, function(found) {
		        	$log.debug('addLink', found);
		        	links.push(found);
		  	    }, toastService.failure);
		  	}
	    },
	    updateLink : function(id, link, service) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.put(id, link.id, link.instance, link.extended, function(found) {
		        	$log.debug('updateLink - before', link);
		        	$log.debug('updateLink - after', found);
		  	    }, toastService.failure);
		  	}
	    },
	    removeLink : function(id, link, service, links) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.delete(id, link.id, link.instance, function(found) {
		  			var toremove = link.instance;
		  			_.remove(links, function(element) {
		  				return element.instance == toremove;
		  			});
		        	$log.debug('removeLink - before', link);
		  	    }, toastService.failure);
		  	}
	    },
	    findAllIdName : function(name, list, service) {
			$log.debug('loading for select ', name, list);
	    	service.findAll(function(data) {
		    	_.forEach(data, function(element) {
		            /**
		             * add this element
		             */
		    		list.push({
		            	'id':element.id,
		            	'name':element.name
		            });
		        });
		    }, toastService.failure);
	    },
	    findAll : function(name, id, list, service, callback) {
			$log.debug('loading ', name, service);
			list.splice(0,list.length)
	    	service.findAll(id, function(data) {
		    	_.forEach(data, function(element) {
		            /**
		             * add this element
		             */
		    		list.push(element);
		        });
				$log.debug('loaded ', name, list);
				if(callback) {
					callback(list);
				}
		    }, toastService.failure);
	    },
	    get : function(id, callback, service) {
	    	service.get(id, function(data) {
	    		callback(data);
		    	$log.debug('[GET/scope] ' + data.name + '#' + data.id);
		    }, toastService.failure);
	    }
  }
return {
	  crud:crud,
	  links:crudLinks,
	  scope: {
		  collections: {
			  new: scopeCrud.new,
			  findAll: scopeCrud.findAll
		  },
		  entity: {
			  get: scopeCrud.get,
			  remove: scopeCrud.remove,
			  save: scopeCrud.save,
			  duplicate: scopeCrud.duplicate
		  },
		  link: {
			  add: scopeCrud.addLink,
			  save: scopeCrud.updateLink,
			  remove: scopeCrud.removeLink
		  },
		  combo: {
			  findAll: scopeCrud.findAllIdName
		  }
	  }
  }
  }])
  .factory('genericPickerService', [ '$log', '$mdDialog', 'toastService', function($log, $mdDialog, toastService) {
	  /**
	   * pickers
	   */
	  var pickers = {
			  confirm : function(title, content, ok, cancel) {
					var confirm = $mdDialog.confirm()
						.title(title)
						.textContent(content)
						.ariaLabel('Confirm')
						.ok(ok)
						.cancel(cancel);
					return $mdDialog.show(confirm);
			  },
			  nodes : function(ev, callback, abort, ctrl) {
			        $mdDialog.show({
			          controller: ctrl,
			          templateUrl: 'js/partials/dialog/nodesDialog.tmpl.html',
			          parent: angular.element(document.body),
			          targetEvent: ev,
			          clickOutsideToClose:true
			        })
			        .then(function(node) {
			        	callback(node);
			        }, function() {
			            abort();
			        });
			  },
			  graph : function(event, anchor, callback, abort, ctrl) {
			        $mdDialog.show({
			          locals: {
			            anchor: anchor
					  },
					  bindToController : true,
			          controller: ctrl,
			          templateUrl: 'js/partials/dialog/graphDialog.tmpl.html',
			          targetEvent: event,
			          clickOutsideToClose:true
			        })
			        .then(function(node) {
			        	callback(node);
			        }, function() {
			            abort();
			        });
			  }
	  }
	  return {
		  pickers : pickers
	  }
  }]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Services */

/**
 * genericScopeService
 */
angular.module('JarvisApp.services.scope', ['JarvisApp.services.generic']).factory('genericScopeService', 
		[ '$log', '$filter', 'genericResourceService', 'genericPickerService', 'toastService', function($log, $filter, genericResourceService, genericPickerService, toastService) {
  var scope = {
	  resources : function(setEntities, getEntities, scope, resource, service, init) {
	    	$log.debug("inject default resources scope", resource, init);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.create = function(create, callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		create,
		        		service,
		        		callback
		        );
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.new = function(callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		init,
		        		service,
		        		callback
		        );
		    }
			scope.crud = genericResourceService.crud(resource.split('/'));
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(entity,callback) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:entity.name, id:entity.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
			    	$log.debug('remove', entity);
					var toremove = entity.id;
					_.remove(getEntities(), function(element) {
						return element.id == toremove;
					});
			    	scope.crud.delete(entity.id);
			    	if(callback) {
			    		callback(entity);
			    	}
		    	}, function() {
			    	$log.debug('abort');
		    	});
		    }
		    /**
		     * loading
		     */
		    scope.load = function(callback) {
		    	setEntities([]);
		    	service.findAll(function(data) {
		    		setEntities(data);
					$log.debug(resource+'-ctrl', data, callback);
					if(callback) {
						callback();
					}
			    }, toastService.failure);
		    }
	  },
	  resource : function(scope, resource, back, service, link, dataLink, stateParamsId) {
	    	$log.debug("inject default resource scope", resource);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
		    			genericResourceService.scope.entity.remove(function() {scope.go(back)}, resource, data, service);
			    	}, function() {
				    	$log.debug('abort');
			   	});
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.save = function(data) {
		    	genericResourceService.scope.entity.save(resource, data, service);
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.duplicate = function(data) {
		    	genericResourceService.scope.entity.duplicate(function() {scope.go(back)}, resource, data, service);
		    }
	  },
	  resourceLink : function(getLink, scope, resource, back, service, link, dataLink, stateParamsId, callbacks) {
	    	$log.debug('inject link',link);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.add = function(data) {
		    	$log.debug('link::add',data);
		    	genericResourceService.scope.link.add(stateParamsId,data,dataLink,link,getLink());
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.update = function(data) {
		    	$log.debug('link::update',data);
		    	genericResourceService.scope.link.save(stateParamsId,data,link);
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.drop = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.remove.link', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
				    	$log.debug('link::drop',data);
				    	if(callbacks && callbacks.remove) {
				    		/**
				    		 * alternate remove function
				    		 */
				    		callbacks.remove(data);
				    	} else {
				    		/**
				    		 * default remove function
				    		 */
					    	genericResourceService.scope.link.remove(stateParamsId,data,link,getLink());
				    	}
				    	/**
				    	 * post drop callback
				    	 */
				    	if(callbacks && callbacks.afterRemove) {
				    		callbacks.afterRemove(data);
				    	}
			    	}, function() {
				    	$log.debug('abort');
			    	}
			    );
			}
	  }
  };
  return {
	  scope:scope
  }
}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Controllers */

angular.module('JarvisApp.config',['JarvisApp.directives.files'])
    .config(['RestangularProvider', function(RestangularProvider) {
		RestangularProvider.setDefaultHeaders({
			'content-type': 'application/json'
		});

		/**
		 * request interceptor
		 */
	    RestangularProvider.setFullRequestInterceptor(function(element, operation, route, url, headers, params, httpConfig) {
	    	headers['content-type'] = 'application/json';
			return {
			  element: element,
			  params: params,
			  headers: headers,
			  httpConfig: httpConfig
			};
	    });
		/**
		 * answer interceptor
		 */
	    RestangularProvider.setResponseExtractor(function(response) {
	    	if(angular.isObject(response)) {
	    	  var newResponse = response;
	    	  newResponse.originalElement = response;
	    	  return newResponse
	    	} else {
	    		return response;
	    	}
	    });
    }])
    .config(['$mdIconProvider', function($mdIconProvider) {
	}])
	.config(['$translateProvider', function($translateProvider){
		// Register a loader for the static files
		// So, the module will search missing translation tables under the specified urls.
		// Those urls are [prefix][langKey][suffix].
		$translateProvider.useStaticFilesLoader({
			prefix: 'js/l10n/',
			suffix: '.json'
		});
		// Tell the module what language to use by default
		$translateProvider.preferredLanguage('fr_FR');
		$translateProvider.useSanitizeValueStrategy(null);
	}])
    /**
     * main controller
     */
    .controller('JarvisAppCtrl',
    		['$rootScope',
    		 '$scope',
    		 '$log',
    		 '$store',
    		 '$notification',
    		 '$http',
    		 '$mdDialog',
    		 '$mdSidenav',
    		 '$mdMedia',
    		 '$location',
    		 '$window',
    		 '$state',
    		 'genericPickerService',
    		 'toastService',
    		 'jarvisWidgetDeviceService',
    		 'jarvisWidgetEventService',
    		 'jarvisWidgetConfigurationService',
    		 'oauth2ResourceService',
    	function(
    			$rootScope,
    			$scope,
    			$log,
    			$store,
    			$notification,
    			$http,
    			$mdDialog,
    			$mdSidenav,
    			$mdMedia,
    			$location,
    			$window,
    			$state,
    			genericPickerService,
    			toastService,
    			deviceResourceService,
    			eventResourceService,
    			configurationResourceService,
    			oauth2ResourceService){
        $scope.isJson = 
			function isJson(str) {
			    try {
			        JSON.parse(str);
			    } catch (e) {
			        return false;
			    }
			    return true;
			};
    	/**
         * default value
         */
        $scope.defaultValue = function(value, def) {
        	if(value === undefined) {
        		return def;
        	}
        	return value
        }
        
        /**
         * load settings
         */
        $scope.loadSettings = function() {
            configurationResourceService.configuration.findAll(function(data) {
            	var config = _.find(data, 'active');
            	if(config) {
                    $scope.config = config;
            	} else {
            		toastService.failure('No default settings');
            	}
    	    }, toastService.failure);
        }

        /**
         * save settings
         */
        $scope.saveSettings = function() {
            configurationResourceService.configuration.put($scope.config, function(data) {
            	$log.info("Updated", data);
    	    }, toastService.failure);
        }

        /**
         * close settings
         */
        $scope.closeSettings = function() {
            $mdDialog.hide();
        }

        $scope.settings = function(ev) {
        	var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'));
        	$mdDialog.show({
        		  scope: $scope,
        		  preserveScope: true,
        	      templateUrl: 'js/partials/dialog/settingsDialog.tmpl.html',
        	      parent: angular.element(document.body),
        	      targetEvent: ev,
        	      clickOutsideToClose:true,
        	      fullscreen: useFullScreen
        	}).then(function() {
     		   		$log.warn("Saving settings");
        	   }, function() {
        		   	$log.warn("Cancel settings save");
        	   }
        	);
        }

        /**
         * create showdown service in global scope
         */
        $scope.markdown = new showdown.Converter({
        	'tables': 'true',
        	'ghCodeBlocks': 'true'
        });
        
        /**
         * helper
         * @param help key
         */
        $scope.helper = function(key) {
        	$log.debug('State', $state.current.name);
        	
        	/**
        	 * read raw resource (partial load)
        	 */
        	$http.get('js/helps/fr/'+$state.current.name+'.markdown').then(function(response) {
        		/**
        		 * render markdown to html
        		 */
        		var html = $scope.markdown.makeHtml(response.data);
            	$scope.help = {"content": html.replace(/<table>/g, '<table class="table table-striped table-bordered table-hover">')};
            	$log.debug('Help', $scope.help);
                $mdSidenav('right').toggle();
        	});
        };

        /**
         * toggle navbar
         * @param menuId
         */
        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
        };

        /**
         * load configuration
         */
        $scope.location = function(menuId, target) {
            $mdSidenav(menuId).toggle();
            $location.path(target);
        };

        /**
         * go to state
         */
        $scope.go = function(target,params) {
            $state.go(target,params);
        };

        /**
         * send an event
         * @param device
         * @param value
         */
        $scope.emit = function(device, value) {
        	$log.debug('JarvisAppCtrl::emit', device, value, device.trigger);
        	eventResourceService.event.post( 
        			{
        				trigger:device.trigger,
		        		timestamp: (new Date()).toISOString(),
		        		fired: true,
		        		number:value
	        		}, function(data) {
		            	$log.debug('JarvisAppCtrl::emit', data);
		       	    	toastService.info('event ' + data.trigger + '#' + data.id + ' emitted');
	        		}, toastService.failure);
        };

        /**
    	 * on server side execute all action on this device
    	 * @param device
    	 */
    	$scope.execute = function(device) {
    	 	deviceResourceService.device.task(device.id, 'execute', {}, function(data) {
    	 		$log.debug('JarvisAppCtrl::execute', data);
    	 	    $scope.renderdata = data;
    	    }, toastService.failure);
    	}

    	/**
         * go to state
         */
        $scope.graph = function(event, anchor) {
	    	return genericPickerService.pickers.graph(
	    			event,
	    			anchor,
	    			function(node) {
	    				$log.debug('Go');
	    			}, function() {
	    				$log.debug('Abort');
	    			},
	    			'graphDialogCtrl'
	    	);
        }

        /**
         * store access
         */
        $scope.$watch(
        	function(classname, instance, def) {
	    		return $store.collection;
	        }, function(newValue, oldValue, scope) {
				$scope.store = newValue;
			});
        
        /**
         * bootstrap this controller
         */
    	$scope.boot = function() {
            /**
             * initialize jarvis configuration
             */
            $scope.config = {};
            $scope.version = {
            		angular: angular.version,
            		angularmd: window.ngMaterial
            };

            $log.info('JarvisAppCtrl',$scope.version);

            $scope.media = $mdMedia('xs');
            $scope.$watch(function() { return $mdMedia('xs'); }, function(media) {
                if(media) $scope.media = 'xs';
            });
            $scope.$watch(function() { return $mdMedia('gt-xs'); }, function(media) {
                if(media) $scope.media = 'gt-xs';
            });
            $scope.$watch(function() { return $mdMedia('sm'); }, function(media) {
                if(media) $scope.media = 'sm';
            });
            $scope.$watch(function() { return $mdMedia('gt-sm'); }, function(media) {
                if(media) $scope.media = 'gt-sm';
            });
            $scope.$watch(function() { return $mdMedia('md'); }, function(media) {
                if(media) $scope.media = 'md';
            });
            $scope.$watch(function() { return $mdMedia('gt-md'); }, function(media) {
                if(media) $scope.media = 'gt-md';
            });
            $scope.$watch(function() { return $mdMedia('lg'); }, function(media) {
                if(media) $scope.media = 'lg';
            });
            $scope.$watch(function() { return $mdMedia('gt-lg'); }, function(media) {
                if(media) $scope.media = 'gt-lg';
            });
            $scope.$watch(function() { return $mdMedia('xl'); }, function(media) {
                if(media) $scope.media = 'xl';
            });
            $scope.$watch(function() { return $mdMedia('print'); }, function(media) {
                if(media) $scope.media = 'print';
            });

            /**
             * highlight JS
             */
            hljs.initHighlightingOnLoad();

            /**
             * load when ctrl init is done
             */
            $scope.loadSettings();

            $log.info('JarvisAppCtrl configured');
    	}
    	/**
    	 * try to connect
    	 */
    	oauth2ResourceService.connect($scope);
    }])
	.controller('extractTokenCtrl',
			['$scope', '$log', '$location', '$rootScope', '$state', 'oauth2ResourceService',
		function($scope, $log, $location, $rootScope, $state, oauth2ResourceService) {
        	$log.warn('extractTokenCtrl', $state);
        	var hash = $location.path().substr(1);
        	var splitted = hash.split('&');
        	var params = {};
        	for (var i = 0; i < splitted.length; i++) {
            	var param  = splitted[i].split('=');
            	var key    = param[0];
            	var value  = param[1];
            	params[key] = value;
        		if(key === 'access_token') {
	            	/**
	            	 * try to connect
	            	 */
	            	oauth2ResourceService.connect($scope, params.access_token);
    	        	$location.path("/home");
        		}
        	}
	}])
	.controller('oauth2DialogCtrl',
			['$scope', '$window', '$log', '$mdDialog', 'oauth2ResourceService', function($scope, $window, $log, $mdDialog, oauth2ResourceService) {
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
			oauth2ResourceService.config(
				{
					"client": answer.client,
					"oauth2_redirect_uri": $window.location.href.split('#')[0]
				},
		    	function(data) {
			        $log.info('JarvisAppCtrl configured with', data);
			        $window.location.href = data.url;
			    },
			    function(error) {
		        	$log.warn('no oauth2 configuration', error);
			    }
			);
			$mdDialog.hide();
		};
	}])
	.controller('graphDialogCtrl',
			['$scope', '$log', '$mdDialog',
		function($scope, $log, $mdDialog) {
		$log.info('graphDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
	}])
	.controller('pickPluginDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickPluginDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Connected Objects",
		    	 selectable : false,
		    	 nodes:[]
		     },
		     {
		    	 name:"Plugin Scripts",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crudDevice = genericResourceService.crud(['devices']);
		$scope.crudDevice.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = false;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
		$scope.crudScript = genericResourceService.crud(['plugins','scripts']);
		$scope.crudScript.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[1].nodes.push(element);
					});
				},
				toastService.failure
		)
	}])
	.controller('pickDeviceDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickDeviceDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Connected Objects",
		    	 selectable : false,
		    	 nodes:[]
		     },
		     {
		    	 name:"Plugin Scripts",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crudDevice = genericResourceService.crud(['devices']);
		$scope.crudDevice.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
		$scope.crudScript = genericResourceService.crud(['plugins','scripts']);
		$scope.crudScript.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = false;
				    	$scope.elementsPicker[1].nodes.push(element);
					});
				},
				toastService.failure
		)
	}])
	.controller('pickCommandDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickCommandDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Commands",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['commands']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickTriggerDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickTriggerDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Triggers",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['triggers']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickNotificationDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickNotificationDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Notifications",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['notifications']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickCronDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickCronDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Crontab objects",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['crons']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickBlockDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
			function($scope, $log, $mdDialog, genericResourceService, toastService) {
			$log.info('pickBlockDialogCtrl');
			$scope.hide = function() {
			   $mdDialog.hide();
			 };
			$scope.cancel = function() {
			  $mdDialog.cancel();
			};
			$scope.answer = function(answer) {
			  $mdDialog.hide(answer);
			};
			$scope.elementsPicker = [
			     {
			    	 name:"Blocks",
			    	 selectable : false,
			    	 nodes:[]
			     }
		    ];
			$scope.crudBlock = genericResourceService.crud(['blocks']);
			$scope.crudBlock.findAll(
					function(elements) {
						_.each(elements, function(element) {
							element.selectable = true;
					    	$scope.elementsPicker[0].nodes.push(element);
						});
					},
					toastService.failure
			);
	}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Directives */

angular.module('JarvisApp.directives.files', [])
    .directive('appVersion', function(version) {
      return function(scope, elm, attrs) {
        elm.text(version);
      };
    })
	.directive('fileModel', ['$parse','$log', function ($parse, $log) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var modelSetter = model.assign;
	            
	            element.bind('change', function(){
	                scope.$apply(function(){
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };
	}])
	.directive('fileSelect', ['$window','$log', function ($window, $log) {
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        link: function (scope, el, attr, ctrl) {
	            var fileReader = new $window.FileReader();
	
	            fileReader.onload = function () {
		        	$log.info('fileSelect.onload');

		        	var base64 = fileReader.result.substr(fileReader.result.lastIndexOf(",")+1);
	            	$log.debug("result:",fileReader.result)
	            	$log.debug("result base64 value:",base64)
	                ctrl.$setViewValue(atob(base64));
	
	                if ('fileLoaded' in attr) {
	                    scope.$eval(attr['fileLoaded']);
	                }
	            };
	
	            fileReader.onprogress = function (event) {
	                if ('fileProgress' in attr) {
	                    scope.$eval(attr['fileProgress'], {'$total': event.total, '$loaded': event.loaded});
	                }
	            };
	
	            fileReader.onerror = function () {
	                if ('fileError' in attr) {
	                    scope.$eval(attr['fileError'], {'$error': fileReader.error});
	                }
	            };
	
	            var fileType = attr['fileSelect'];
	
	            el.bind('change', function (e) {
		        	$log.info('fileSelect.change');

		        	var fileName = e.target.files[0];
	
	                if (fileType === '' || fileType === 'text') {
	                    fileReader.readAsText(fileName, 'UTF-8');
	                } else if (fileType === 'data') {
	                    fileReader.readAsDataURL(fileName);
	                }
	            });
	        }
	    };
	}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Filters */

angular.module('JarvisApp.filters', []).
    filter('interpolate', ['version', function(version) {
      return function(text) {
        return String(text).replace(/\%VERSION\%/mg, version);
      }
    }]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Controllers */

angular.module('JarvisApp.routes',['JarvisApp.config'])
    .config( ['$urlRouterProvider', function($urlRouterProvider) {
        /**
         * default state
         */
        $urlRouterProvider.otherwise('/home');
    }])
    .config(['$stateProvider', function($stateProvider) {
        /**
         * now set up the state
         */
        $stateProvider
        .state('token_access', {
            url: '/access_token=:accesToken',
            controller: 'extractTokenCtrl',
            template: ''
        })
        .state('helper-devices', {
            url: '/helper-devices',
            params: {
            	resources: ['crons','triggers','devices','plugins','commands']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('helper-scenarii', {
            url: '/helper-scenarii',
            params: {
            	resources: ['triggers','crons','scenarios','blocks','plugins']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('helper-system', {
            url: '/helper-system',
            params: {
            	resources: ['configurations','notifications','properties','connectors','views']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('home', {
            url: '/home',
            controller: 'jarvisWidgetHomeCtrl',
            template: '<jarvis-widget-home></jarvis-widget-home>'
        })
        .state('events', {
            url: '/events',
            controller: 'jarvisWidgetEventsCtrl',
            template: '<jarvis-widget-event></jarvis-widget-event>'
        })
        .state('triggers', {
            url: '/triggers',
            controller: 'jarvisWidgetTriggersCtrl',
            template: '<jarvis-widget-triggers></jarvis-widget-triggers>'
        })
        .state('triggers-by-id', {
            url: '/triggers/:id?tab',
            controller: 'jarvisWidgetTriggerCtrl',
            template: '<jarvis-widget-trigger></jarvis-widget-trigger>'
        })
        .state('notifications', {
            url: '/notifications',
            controller: 'notificationsCtrl',
            template: '<jarvis-widget-notifications></jarvis-widget-notifications>'
        })
        .state('notifications-by-id', {
            url: '/notifications/:id?tab',
            controller: 'notificationCtrl',
            template: '<jarvis-widget-notification></jarvis-widget-notification>'
        })
        .state('devices', {
            url: '/devices',
            controller: 'devicesCtrl',
            template: '<jarvis-widget-devices></jarvis-widget-devices>'
        })
        .state('devices-by-id', {
            url: '/devices/:id?tab',
            controller: 'deviceCtrl',
            template: '<jarvis-widget-device></jarvis-widget-device>'
        })
        .state('plugins', {
            url: '/plugins',
            controller: 'pluginsCtrl',
            template: '<jarvis-widget-plugins></jarvis-widget-plugins>'
        })
        .state('plugins-by-id-script', {
            url: '/plugins/scripts/:id?tab',
            controller: 'pluginScriptCtrl',
            template: '<jarvis-widget-plugin></jarvis-widget-plugin>'
        })
        .state('commands', {
            url: '/commands',
            controller: 'commandsCtrl',
            template: '<jarvis-widget-commands></jarvis-widget-commands>'
        })
        .state('commands-by-id', {
            url: '/commands/:id?tab',
            controller: 'commandCtrl',
            template: '<jarvis-widget-command></jarvis-widget-command>'
        })
        .state('views', {
            url: '/views',
            controller: 'viewsCtrl',
            template: '<jarvis-widget-views></jarvis-widget-views>'
        })
        .state('views-by-id', {
            url: '/views/:id?tab',
            controller: 'viewCtrl',
            template: '<jarvis-widget-view></jarvis-widget-view>'
        })
        .state('configurations', {
            url: '/configurations',
            controller: 'configurationsCtrl',
            template: '<jarvis-widget-configurations></jarvis-widget-configurations>'
        })
        .state('configurations-by-id', {
            url: '/configurations/:id?tab',
            controller: 'configurationCtrl',
            template: '<jarvis-widget-configuration></jarvis-widget-configuration>'
        })
        .state('properties', {
            url: '/properties',
            controller: 'propertiesCtrl',
            template: '<jarvis-widget-properties></jarvis-widget-properties>'
        })
        .state('properties-by-id', {
            url: '/properties/:id?tab',
            controller: 'propertyCtrl',
            template: '<jarvis-widget-property></jarvis-widget-property>'
        })
        .state('connectors', {
            url: '/connectors',
            controller: 'connectorsCtrl',
            template: '<jarvis-widget-connectors></jarvis-widget-connectors>'
        })
        .state('connectors-by-id', {
            url: '/connectors/:id?tab',
            controller: 'connectorCtrl',
            template: '<jarvis-widget-connector></jarvis-widget-connector>'
        })
        .state('snapshots', {
            url: '/snapshots',
            controller: 'snapshotsCtrl',
            template: '<jarvis-widget-snapshots></jarvis-widget-snapshots>'
        })
        .state('snapshots-by-id', {
            url: '/snapshots/:id?tab',
            controller: 'snapshotCtrl',
            template: '<jarvis-widget-snapshot></jarvis-widget-snapshot>'
        })
        .state('crons', {
            url: '/crons',
            controller: 'cronsCtrl',
            template: '<jarvis-widget-crons></jarvis-widget-crons>'
        })
        .state('crons-by-id', {
            url: '/crons/:id?tab',
            controller: 'cronCtrl',
            template: '<jarvis-widget-cron></jarvis-widget-cron>'
        })
        .state('scenarios', {
            url: '/scenarios',
            controller: 'scenariosCtrl',
            template: '<jarvis-widget-scenarios></jarvis-widget-scenarios>'
        })
        .state('scenarios-by-id', {
            url: '/scenarios/:id?tab',
            controller: 'scenarioCtrl',
            template: '<jarvis-widget-scenario></jarvis-widget-scenario>'
        })
        .state('blocks', {
            url: '/blocks',
            controller: 'blocksCtrl',
            template: '<jarvis-widget-blocks></jarvis-widget-blocks>'
        })
        .state('blocks-by-id', {
            url: '/blocks/:id?tab',
            controller: 'blockCtrl',
            template: '<jarvis-widget-block></jarvis-widget-block>'
        })
        ;
    }]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Cf. https://angular-md-color.com */

angular.module('JarvisApp.theme', [ 'ngMaterial' ]).config([ '$mdThemingProvider', function ($mdThemingProvider) {
	$mdThemingProvider.alwaysWatchTheme(true);
    var customPrimary = {
            '50': '#aaeafd',
            '100': '#92e4fc',
            '200': '#79defc',
            '300': '#60d8fb',
            '400': '#47d2fb',
            '500': '#2ECCFA',
            '600': '#15c6f9',
            '700': '#06bbef',
            '800': '#05a7d6',
            '900': '#0594bd',
            'A100': '#c3f0fe',
            'A200': '#dcf7fe',
            'A400': '#f5fdff',
            'A700': '#0480a5'
        };
        $mdThemingProvider
            .definePalette('customPrimary', 
                            customPrimary);

        var customAccent = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#f8fcfe',
            '400': '#e3f4f9',
            '500': '#CEECF5',
            '600': '#b9e4f1',
            '700': '#a4dcec',
            '800': '#8ed3e8',
            '900': '#79cbe4',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#64c3df'
        };
        $mdThemingProvider
            .definePalette('customAccent', 
                            customAccent);

        var customWarn = {
            '50': '#ff8080',
            '100': '#ff6666',
            '200': '#ff4d4d',
            '300': '#ff3333',
            '400': '#ff1a1a',
            '500': '#FF0000',
            '600': '#e60000',
            '700': '#cc0000',
            '800': '#b30000',
            '900': '#990000',
            'A100': '#ff9999',
            'A200': '#ffb3b3',
            'A400': '#ffcccc',
            'A700': '#800000'
        };
        $mdThemingProvider
            .definePalette('customWarn', 
                            customWarn);

        var customBackground = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#ffffff',
            '400': '#ffffff',
            '500': '#F2F2F2',
            '600': '#e5e5e5',
            '700': '#d8d8d8',
            '800': '#cccccc',
            '900': '#bfbfbf',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#b2b2b2'
        };
        $mdThemingProvider
            .definePalette('customBackground', 
                            customBackground);

       $mdThemingProvider.theme('default')
           .primaryPalette('customPrimary')
           .accentPalette('customAccent')
           .warnPalette('customWarn')
           .backgroundPalette('customBackground')
    }]);/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Controllers */

angular.module('JarvisApp.websocket',['angular-websocket'])
    // WebSocket works as well
    .factory('$notification', [ '$store', '$log', '$websocket', '$window', function($store, $log, $websocket, $window) {
      $log.info("$notification", $window.location);
      // Open a WebSocket connection
      var dataStream = $websocket('ws://'+$window.location.hostname+':'+$window.location.port+'/stream/');

      dataStream.onMessage(function(message) {
    	var entity = JSON.parse(message.data);
    	$store.push(entity.classname, entity.instance, entity.data);
      });

      var methods = {
    	dataStream: dataStream
      };

      return methods;
    }]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Directives */

angular.module('JarvisApp.directives.widgets', ['JarvisApp.services'])
.directive('jarvisDefaultMenuBar', [ '$log', '$parse', function ($log, $parse) {
	return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/tools/jarvis-default-menu-bar.html',
	    require:"ngModel",
	    link: function($scope, elm, attrs, ngModel) {
	    	$scope.closeMenu = attrs.close;
	    	$scope.element = function () {
                return ngModel.$viewValue;
            }
	    }
	}
}])
.directive('jarvisPickElement', [ '$log', '$parse', 'genericPickerService', function ($log, $parse, genericPickerService) {
	return {
	    restrict: 'E',
	    template: '<md-button aria-label="menu" class="md-icon-button" ng-click="pickItemDialog($event)"><md-tooltip>{{pick.tooltips|translate}}</md-tooltip><md-icon md-font-icon="material-icons md-16">{{pick.icon}}</md-icon></md-button>',
	    scope: {},
	    link: function(scope, elm, attrs, ctrl) {
	    	scope.pick = {};
	    	scope.pick.tooltips = attrs.tooltips;
	    	scope.pick.icon = attrs.icon;
	    	scope.pick.tooltips = attrs.tooltips;
	    	scope.pick.ctrl = attrs.ctrl;
	    	scope.pick.callback = attrs.callback;
	        scope.pickItemDialog = function(ev, node) {
	        	return genericPickerService.pickers.nodes(ev, function(node) {
	        		$parse(scope.pick.callback)(scope.$parent)(node);
	        	}, function() {
	        		$log.debug('no picked element');
	        	},
	        	scope.pick.ctrl);
	        }
	    }
	}
}])
.directive('typecommand', [ '$log', function ($log) {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
			ctrl.$validators.typecommand = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					// consider empty models to be valid
					return false;
				}
				if(modelValue == 'data' || modelValue == 'action') {
					return true;
				}
				// it is invalid
			    return false;
			};
	    }
	}
}])
.directive('naturecommand', [ '$log', function ($log) {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
			ctrl.$validators.naturecommand = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					// consider empty models to be valid
					return false;
				}
				if(modelValue == 'text' || modelValue == 'json' || modelValue == 'xml') {
					return true;
				}
				// it is invalid
			    return false;
			};
	    }
	}
}])
.directive('jarvisCard', [ '$log', '$parse', function ($log, $parse) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-card.html',
    scope: false,
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-card element', $parse(attrs.element)(scope));
    	$log.debug('jarvis-card state', $parse(attrs.state)(scope));
    	scope.element = $parse(attrs.element)(scope);
    	scope.state = $parse(attrs.state)(scope);
    }
  }
}])
.directive('jarvisTile', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile');
    }
  }
}])
.directive('jarvisTileBoolean', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-boolean.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-boolean');
    }
  }
}])
.directive('jarvisTileInt', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-int.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-int');
    }
  }
}])
.directive('jarvisTilePercent', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-percent.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-percent');
    }
  }
}])
.directive('jarvisInlineTemplate', [ '$log', '$stateParams', '$parse', function ($log, $stateParams, $parse) {
  return {
    restrict: 'E',
    template: '<div ng-include="getJarvisInlineTemplateUrl()"></div>',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-inline-template id', $parse(attrs.id)(scope));
    	scope.getJarvisInlineTemplateUrl = function() {
    		/**
    		 * update scope
    		 */
        	scope.data = $parse(attrs.data)(scope);
            return '/api/directives/html/devices/'+$parse(attrs.id)(scope);
        }
    }
  }
}])
.directive('jarvisGauge', [ '$log', '$store', '$notification', function ($log, $store, $notification) {
  return {
    restrict: 'AC',
    controller: [ '$scope', '$element', '$attrs', function($scope, $element, $attrs) {
    	$log.debug('jarvis-gauge', $attrs.id);
    	var opts = {
    			  fontSize: 10,
    			  lines: 10, // The number of lines to draw
    			  angle: 0.0, // The length of each line
    			  lineWidth: 0.32, // The line thickness
    			  pointer: {
    			    length: 0.9, // The radius of the inner circle
    			    strokeWidth: 0.035, // The rotation offset
    			    color: '#000000' // Fill color
    			  },
    			  limitMax: 'false',   // If true, the pointer will not go past the end of the gauge
    			  colorStart: '#6FADCF',   // Colors
    			  colorStop: '#8FC0DA',    // just experiment with them
    			  strokeColor: '#E0E0E0',   // to see which ones work best for you
    			  generateGradient: true
    			};
		$scope.gauge = new Gauge(document.getElementById($attrs.id)).setOptions(opts); // create sexy gauge!
		$scope.gauge.maxValue = 100; // set max gauge value
		$scope.gauge.animationSpeed = 10; // set animation speed (32 is default value)
		$scope.gauge.set(0); // set actual value
		
		$scope.$watch(
			function() {
				return $store.get('SystemIndicator','1',0).systemCpuLoad * 100;
			}, function(newValue, oldValue, scope) {
				$scope.gauge.set(newValue);
			});
    }]
  }
}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.block', ['JarvisApp.services'])
.controller('blocksCtrl', 
		[ '$scope',
		  '$log',
		  'genericScopeService',
		  'genericPickerService',
		  'jarvisWidgetBlockService',
		  'toastService', 
	function(
			$scope,
			$log,
			genericScopeService,
			genericPickerService,
			componentService,
			toastService){
	$log.debug('blocks-ctrl', $scope.blocks);
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.blocks = entities;
			},
			function() {
				return $scope.blocks;
			},
			$scope, 
			'blocks', 
			componentService.block,
			{
    			name: "block name",
    			icon: "list",
    			parameter: "{}"
    		}
	);

	$log.debug('blocks-ctrl', $scope.blocks);
}])
.controller('blockCtrl',
	[ '$scope',
	  '$log',
	  '$stateParams',
	  'genericScopeService',
	  'genericResourceService',
	  'genericPickerService',
	  'jarvisWidgetBlockService',
	  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			componentService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'block', 
			'blocks', 
			componentService.block
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins:{
				"if": {},
				"then": {},
				"else": {}
			},
			blocks:{
				"then": {},
				"else": {}
			}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.if;
			},
			$scope.links.plugins.if,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.if, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.then;
			},
			$scope.links.plugins.then,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.else;
			},
			$scope.links.plugins.else,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.else, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.then;
			},
			$scope.links.blocks.then,
			'block',
			'blocks',
			componentService.block, 
			componentService.blocks.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.else;
			},
			$scope.links.blocks.else,
			'block',
			'blocks',
			componentService.block, 
			componentService.blocks.else, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * task
     */
    $scope.test = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		componentService.block.task(block.id, 'test', {}, function(data) {
       	    	$scope.testExpression = data == "true";
    	    }, toastService.failure);
    	}
    }
    /**
     * task
     */
    $scope.execute = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		componentService.block.task(block.id, 'execute', {}, function(data) {
       	    	$log.debug('[BLOCK/test]', block, data);
       	    	$scope.testExpression = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.block=update}, componentService.block);

		/**
		 * get all plugins
		 */
    	$scope.plugins = {
    			if:{},
    			then:{},
    			else:{}
    	};
    	$scope.plugins.if = [];
    	genericResourceService.scope.collections.findAll('plugins.if', $stateParams.id, $scope.plugins.if, componentService.plugins.if);
    	$scope.plugins.then = [];
    	genericResourceService.scope.collections.findAll('plugins.then', $stateParams.id, $scope.plugins.then, componentService.plugins.then);
    	$scope.plugins.else = [];
    	genericResourceService.scope.collections.findAll('plugins.else', $stateParams.id, $scope.plugins.else, componentService.plugins.else);

		/**
		 * get all plugins
		 */
    	$scope.blocks = {
    			then:{},
    			else:{}
    	};
    	$scope.blocks.then = [];
    	genericResourceService.scope.collections.findAll('blocks.then', $stateParams.id, $scope.blocks.then, componentService.blocks.then);
    	$scope.blocks.else = [];
    	genericResourceService.scope.collections.findAll('blocks.else', $stateParams.id, $scope.blocks.else, componentService.blocks.else);

    	$log.debug('block-ctrl', $scope.block);
    	$log.debug('block-ctrl', $scope.blocks);
    }
}])
.factory('jarvisWidgetBlockService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		    block: genericResourceService.crud(['blocks']),
		    plugins: {
		    	"if": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_IF'),
			    "then": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_THEN'),
			    "else": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_ELSE')
		    },
			blocks: {
				"then": genericResourceService.links(['blocks'], ['blocks'],'HREF_THEN'),
				"else": genericResourceService.links(['blocks'], ['blocks'],'HREF_ELSE')
			}
	  }
}])
/**
 * blocks
 */
.directive('jarvisWidgetBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/jarvis-widget-blocks.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * block
 */
.directive('jarvisWidgetBlock', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/jarvis-widget-block.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockElse', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-else.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockThen', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-then.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Directives */

angular.module('jarvis.directives.command', ['JarvisApp.services'])
.controller('commandsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetCommandService',
	function($scope, $log, genericScopeService, jarvisWidgetCommandService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.commands = entities;
			},
			function() {
				return $scope.commands;
			},
			$scope, 
			'commands', 
			jarvisWidgetCommandService.command,
			{
    			name: "command name",
    			icon: "list"
    		}
	);
}])
.controller('commandCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'jarvisWidgetCommandService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, jarvisCommandService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'command', 
			'commands', 
			jarvisCommandService.command
	);
	/**
	 * declare links
	 */
	$scope.links = {
			notifications: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.notifications;
			},
			$scope.links.notifications,
			'notification',
			'notifications',
			jarvisCommandService.command, 
			jarvisCommandService.notifications, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * execute this command
     * @param command, the command to execute
     */
    $scope.execute = function(command) {
    	jarvisWidgetCommandService.command.task(command.id, 'execute', $scope.rawTestData, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' executed');
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * test this command
     * @param command, the command to execute
     */
    $scope.test = function(command) {
    	jarvisWidgetCommandService.command.task(command.id, 'test', $scope.rawTestData, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' tested');
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * transform command
     */
    $scope.pretty = function() {
    	$log.debug('pretty', $scope.rawinput);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * fix params
     */
    $scope.submit = function() {
    	$scope.rawTestData = angular.fromJson($scope.editTestData);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * clear params
     */
    $scope.clear = function() {
    	$scope.rawTestData = {"default":"todo"};
    	$scope.editTestData = angular.toJson($scope.rawTestData, true);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    }
    /**
     * loading
     */
    $scope.load = function() {
	    /**
	     * init part
	     */
		$scope.combo = {
				visibles: jarvisCommandService.bool,
				types: jarvisCommandService.types
		}
		/**
		 * input test
		 */
		$scope.clear();
		/**
		 * get current command
		 */
		$scope.commands = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.command=update}, jarvisCommandService.command);
	
		/**
		 * get all crontabs
		 */
    	$scope.notifications = [];
    	genericResourceService.scope.collections.findAll('notifications', $stateParams.id, $scope.notifications, jarvisCommandService.notifications);

		$log.info('command-ctrl');
    }
}])
.factory('jarvisWidgetCommandService', [ 'genericResourceService', function( genericResourceService) {
	return {
	  	command: genericResourceService.crud(['commands']),
	  	notifications : genericResourceService.links(['commands'], ['notifications']),
	  	bool: [
           	   {
           		   id: true,
           		   value:'common.true'
           	   },
           	   {
           		   id: false,
           		   value:'common.false'
           	   }
        ],
	  	types: [
           	   {
          		   id: 'SHELL',
          		   value:'command.shell'
          	   },
          	   {
          		   id: 'COMMAND',
          		   value:'command.single'
          	   },
          	   {
          		   id: 'GROOVY',
          		   value:'command.groovy'
          	   },
          	   {
          		   id: 'SSH',
          		   value:'command.ssh'
          	   },
          	   {
          		   id: 'ZWAY',
          		   value:'command.zway'
          	   }
        ]
	}
}])
/**
 * commands
 */
.directive('jarvisWidgetCommands', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/jarvis-widget-commands.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCommands', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-commands.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * command
 */
.directive('jarvisWidgetCommand', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/jarvis-widget-command.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCommand', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command');
    }
  }
}])
.directive('jarvisCommandNotification', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-notification.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-notification');
    }
  }
}])
.directive('jarvisCommandInput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-input.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-input');
    }
  }
}])
.directive('jarvisCommandScript', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-script.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-script');
    }
  }
}])
.directive('jarvisCommandOutput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-output.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-output');
    }
  }
}]);

/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.configuration', ['JarvisApp.services'])
.controller('configurationsCtrl', 
		['$scope', '$log', 'genericScopeService', 'jarvisWidgetConfigurationService',
	function($scope, $log, genericScopeService, configurationResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.configurations = entities;
			},
			function() {
				return $scope.configurations;
			},
			$scope, 
			'configurations', 
			configurationResourceService.configuration,
			{
    			name: "default",
    			opacity: "1",
    			backgroundUrl: "http://artroyalephotography.com/wp-content/uploads/2011/08/minimal-gray-to-white-gradient-wallpapers_33797_1920x1200-1024x640.jpg"
    		}
	);
}])
.controller('configurationCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'jarvisWidgetConfigurationService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, configurationResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'configuration', 
			'configurations', 
			configurationResourceService.configuration);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current configuration
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.config=update;
    	}, configurationResourceService.configuration);

    	$log.info('configuration-ctrl', $scope.configurations);
    }
}])
.factory('jarvisWidgetConfigurationService', [ 'genericResourceService', function( genericResourceService) {
	return {
	  configuration : genericResourceService.crud(['configurations'])
	}
}])
.directive('jarvisWidgetConfigurations', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/configuration/jarvis-widget-configurations.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConfigurations', [ '$log', '$stateParams', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/directives/configuration/partials/jarvis-configurations.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-configurations');
	    }
	  }
}])
.directive('jarvisWidgetConfiguration', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/configuration/jarvis-widget-configuration.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConfiguration', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/configuration/partials/jarvis-configuration-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-configuration');
    }
  }
}])
.directive('jarvisConfigurationSystem', [ '$log', '$stateParams', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/directives/configuration/partials/jarvis-configuration-system.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-configuration-system');
	    }
	  }
}])
;
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.connector', ['JarvisApp.services'])
.controller('connectorsCtrl', 
		['$scope', '$log', 'genericScopeService', 'jarvisWidgetConnectorService',
	function($scope, $log, genericScopeService, connectorResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.connectors = entities;
			},
			function() {
				return $scope.connectors;
			},
			$scope, 
			'connectors', 
			connectorResourceService.connector,
			{
    			name: "connector name",
    			icon: "settings_input_antenna",
    			isRenderer: false,
    			isSensor: false,
    			canAnswer: false
    		}
	);
}])
.controller('connectorCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'jarvisWidgetConnectorService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, connectorResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'connector', 
			'connectors', 
			connectorResourceService.connector);
    /**
     * verify this connector
     */
    $scope.ping = function(connector) {
    	connectorResourceService.connector.task(connector.id, 'ping', connector, function(data) {
    		/**
    		 * check data for ping result
    		 */
    		$scope.status = angular.toJson(data, true);
    	}, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current connector
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.connector=update;
    	}, connectorResourceService.connector);

    	$log.info('connector-ctrl', $scope.connectors);
    }
}])
.factory('jarvisWidgetConnectorService', [ 'genericResourceService', function( genericResourceService) {
	return {
		connector: genericResourceService.crud(['connectors'])
	}
}])
/**
 * commands
 */
.directive('jarvisWidgetConnectors', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/jarvis-widget-connectors.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConnectors', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/partials/jarvis-connectors.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * command
 */
.directive('jarvisWidgetConnector', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/jarvis-widget-connector.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConnectorGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/partials/jarvis-connector-general.html',
    link: function(scope, element, attrs) {
    }
  }
}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.cron', ['JarvisApp.services'])
.controller('cronsCtrl', 
		['$scope', '$log', 'genericScopeService', 'jarvisWidgetCronService', 'toastService',
	function($scope, $log, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.crons = entities;
			},
			function() {
				return $scope.crons;
			},
			$scope, 
			'crons', 
			componentService.cron,
			{
    			name: "cron name",
    			icon: "list",
    			cron: "* * * * *"
    		}
	);
    /**
     * start all crontab
     */
    $scope.start = function() {
    	/**
    	 * iterate on each cron
    	 */
    	_.each($scope.crons, function(cron) {
    		componentService.cron.task(cron.id, 'toggle', {target:true}, function(data) {
	   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
		    }, toastService.failure);
    	});
    }
    /**
     * stop all crontab
     */
    $scope.stop = function() {
    	/**
    	 * iterate on each cron
    	 */
    	_.each($scope.crons, function(cron) {
    		componentService.cron.task(cron.id, 'toggle', {target:false}, function(data) {
	   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
		    }, toastService.failure);
    	});
    }
}])
.controller('cronCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'jarvisWidgetCronService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'cron', 
			'crons', 
			componentService.cron);
    /**
     * toggle cron status
     */
    $scope.toggle = function(cron) {
    	$log.info(cron);
    	componentService.cron.task(cron.id, 'toggle', {}, function(data) {
   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
	    }, toastService.failure);
    }
    /**
     * test cron status
     */
    $scope.test = function(cron) {
    	$log.info(cron);
    	componentService.cron.task(cron.id, 'test', {}, function(data) {
   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
	    }, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current cron
		 */
    	$scope.crons = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.cron=update}, componentService.cron);

		$log.info('cron-ctrl', $scope.crons);
    }
}])
.factory('jarvisWidgetCronService', [ 'genericResourceService', function(genericResourceService) {
	return {
		  cron : genericResourceService.crud(['crons'])
	}
}])
/**
 * crons
 */
.directive('jarvisWidgetCrons', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/cron/jarvis-widget-crons.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCrons', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/cron/partials/jarvis-crons.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * cron
 */
.directive('jarvisWidgetCron', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/cron/jarvis-widget-cron.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCron', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/cron/partials/jarvis-cron-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command');
    }
  }
}])
;
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.device', ['JarvisApp.services'])
.controller('devicesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetDeviceService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.devices = entities;
			},
			function() {
				return $scope.devices;
			},
			$scope, 
			'devices', 
			componentService.device,
			{
    			name: "object name",
    			icon: "list"
    		}
	);
}])
.controller('deviceCtrl',
		['$scope', '$log', '$state', '$stateParams', 'jarvisWidgetDeviceService', 'genericScopeService', 'genericResourceService', 'toastService',
	function($scope, $log, $state, $stateParams, componentService, genericScopeService, genericResourceService, toastService){
	$scope.getLink = function() {
		return $scope.plugins;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'device', 
			'devices', 
			componentService.device
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins: {},
			triggers: {},
			devices: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins;
			},
			$scope.links.plugins,
			'device',
			'devices',
			componentService.device, 
			componentService.plugins, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'device',
			'devices',
			componentService.device, 
			componentService.triggers, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.devices;
			},
			$scope.links.devices,
			'device',
			'devices',
			componentService.device, 
			componentService.devices, 
			{'order':'1'},
			$stateParams.id
	);
    /**
	 * render this device, assume no args by default
	 * @param device, the device to render
	 */
	$scope.render = function(device) {
		componentService.device.task(device.id, 'render', {}, function(data) {
	 		$log.debug('deviceCtrl::render', data);
	 	    $scope.renderdata = data;
	 	    $scope.output = angular.toJson(data, true);
	    }, toastService.failure);
	}
	/**
	 * init this controller
	 */
	$scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
	 
		/**
		 * get current device
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.device=update}, componentService.device);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, componentService.plugins);
    	$scope.devices = [];
    	genericResourceService.scope.collections.findAll('devices', $stateParams.id, $scope.devices, componentService.devices);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, componentService.triggers);
	
		$log.info('device-ctrl');
	}
}])
.factory('jarvisWidgetDeviceService', [ 'genericResourceService', function(genericResourceService) {
	return {
		device: genericResourceService.crud(['devices']),
		plugins : genericResourceService.links(['devices'], ['plugins','scripts']),
		devices : genericResourceService.links(['devices'], ['devices']),
		triggers : genericResourceService.links(['devices'], ['triggers'])
	}
}])
/**
 * devices
 */
.directive('jarvisWidgetDevices', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/jarvis-widget-devices.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDevices', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-devices.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * device
 */
.directive('jarvisWidgetDevice', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/jarvis-widget-device.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDeviceGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDevicePlugin', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-plugin.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDeviceRender', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-render.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.event', ['JarvisApp.services'])
.controller('eventsCtrl', 
		['$scope', '$log', 'genericScopeService', 'genericResourceService', 'jarvisWidgetEventService',
	function($scope, $log, genericScopeService, genericResourceService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.events = entities;
			},
			function() {
				return $scope.events;
			},
			$scope, 
			'events',
			componentService.event
	);
	/**
	 * some crud
	 */
	$scope.crud = genericResourceService.crud(['events']);
}])
.factory('jarvisWidgetEventService', [ 'genericResourceService', function(genericResourceService) {
	return {
		event: genericResourceService.crud(['events'])
	}
}])
/**
 * events
 */
.directive('jarvisWidgetEvents', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/events/jarvis-widget-events.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisEvents', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/events/jarvis-events.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.home', ['JarvisApp.services'])
.controller('jarvisWidgetHomeCtrl', 
		['$scope', '$rootScope', '$store', '$log', 'jarvisWidgetViewService', 'jarvisWidgetDeviceService', 'toastService', 'oauth2ResourceService',
	function($scope, $rootScope, $store, $log, jarvisWidgetViewService, jarvisWidgetDeviceService, toastService, oauth2ResourceService){
    /**
     * swipe left
     */
    $scope.onSwipeLeft = function() {
    	if($scope.tabIndex >= ($scope.views.length-1)) {
    		$scope.tabIndex = $scope.views.length-1;
    		return;
    	}
    	$scope.tabIndex++;
    }
    /**
     * swipe right
     */
    $scope.onSwipeRight = function() {
    	if($scope.tabIndex <= 0) {
    		$scope.tabIndex = 0;
    		return;
    	}
    	$scope.tabIndex--;
    }
    /**
     * select tab callback
     */
    $scope.selectTab = function(view, index) {
    	$scope.tabIndex = index;
    }
    /**
     * is selected
     */
    $scope.activeTab = function(view, index) {
    	return $scope.tabIndex == index;
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.store = $store;
    	$scope.views = [];
    	$scope.tabIndex = -1;
    	
	    /**
	     * loading views
	     */
    	jarvisWidgetViewService.view.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * load this view only if ishome
	             */
	            if(element.ishome) {
	            	element.urlBackground = "url('"+element.url+"')";
	            	arr.push(element);
	            }
	        });
	    	toastService.info(arr.length + ' view(s)');
	    	
	    	_.forEach(arr, function(view) {
	    		$log.info('loading view', view);
	    		jarvisWidgetViewService.devices.findAll(view.id, function(data) {
	    			view.devices = data;
	    			var done = _.after(view.devices.length, function() {
	    				$log.debug('Linked devices to view', view.devices);
	    			});
	    			_.forEach(view.devices, function(device){
	    				/**
	    				 * render each view
	    				 */
	    				jarvisWidgetDeviceService.device.task(device.id, 'render', {}, function(data) {
	    		      		device.render = data;
	    		      		done(device);
	    		      	}, toastService.failure);
	    			});
	    	    }, toastService.failure);
			});
	    	
	        $scope.views = arr;
	        if($scope.views.length > 0) {
	        	$scope.tabIndex = 0;
	        }
	    }, toastService.failure);
	
		$log.info('home-ctrl');
    }
}])
.controller('helperCtrl', 
		[ '$scope', '$store', '$log', 'jarvisWidgetViewService', 'jarvisWidgetDeviceService', 'toastService',
	function($scope, $store, $log, jarvisWidgetViewService, jarvisWidgetDeviceService, toastService){
}])
.factory('jarvisWidgetHomeService', [ 'genericResourceService', function(genericResourceService) {
	return {
	}
}])
.directive('jarvisWidgetHome', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/home/jarvis-widget-home.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisHome', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/home/partials/jarvis-home.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Directives */

angular.module('jarvis.directives.navigator', ['JarvisApp.services'])
.controller('jarvisWidgetNavigatorCtrl', 
		[ '$scope', '$log', '$stateParams', function($scope, $log, $stateParams){
		$scope.resources = $stateParams.resources;
		$scope.answer = function(element) {
			element.callback(element);
		}
}])
.factory('jarvisWidgetNavigatorService', [
			'$q',
			'$location',
			'$log',
			'jarvisWidgetSnapshotService',
			'jarvisWidgetBlockService',
			'jarvisWidgetConfigurationService',
			'jarvisWidgetPropertyService',
			'jarvisWidgetViewService',
			'jarvisWidgetTriggerService',
			'jarvisWidgetScenarioService',
			'jarvisWidgetCronService',
			'jarvisWidgetConnectorService',
			'jarvisWidgetCommandService',
			'jarvisWidgetPluginService',
			'jarvisWidgetDeviceService',
	function(
			$q,
			$location,
			$log,
			snapshotResourceService,
			blockResourceService,
			configurationResourceService,
			propertyResourceService,
			viewResourceService,
			triggerResourceService,
			scenarioService,
			cronResourceService,
			connectorResourceService,
			commandService,
			pluginResourceService,
			deviceResourceService) {
	return {
		/**
		 * find all devices
		 */
		devices: function() {
			var deferred = $q.defer();
			var devices = [];
	    	deviceResourceService.device.findAll(function(devices) {
				_.each(devices, function(device) {
					deviceResourceService.plugins.findAll(device.id,function(plugins) {
						device.desc = "";
						_.each(plugins, function(plugin) {
							device.desc += plugin.name;
						});
			    	});
					device.selectable = true;
					device.callback = function(node) {
						 $location.path("/devices/" + node.id);
			    	}
					device.ext = angular.toJson(angular.fromJson(device.parameters),true);
				});
	    		deferred.resolve({elements: devices, title: "Devices", route: "/devices"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all plugins
		 */
		plugins: function() {
			var deferred = $q.defer();
			pluginResourceService.scripts.findAll(function(plugins) {
				_.each(plugins, function(plugin) {
					pluginResourceService.commands.findAll(plugin.id,function(commands) {
						plugin.desc = "";
						_.each(commands, function(command) {
							plugin.desc += command.name;
						});
			    	});
					plugin.selectable = true;
					plugin.callback = function(node) {
						 $location.path("/plugins/scripts/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: plugins, title: "Plugins", route: "/plugins"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all commands
		 */
		commands: function() {
			var deferred = $q.defer();
			commandService.command.findAll(function(commands) {
				_.each(commands, function(command) {
					command.ext = command.body;
					command.selectable = true;
					command.callback = function(node) {
						 $location.path("/commands/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: commands, title: "Commands", route: "/commands"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all snapshots
		 */
		snapshots: function() {
			var deferred = $q.defer();
			snapshotResourceService.snapshot.findAll(function(snapshots) {
				_.each(snapshots, function(snapshot) {
					snapshot.selectable = true;
					snapshot.callback = function(node) {
						 $location.path("/snapshots/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: snapshots, title: "Snapshots", route: "/snapshots"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all blocks
		 */
		blocks: function() {
			var deferred = $q.defer();
			blockResourceService.block.findAll(function(blocks) {
				_.each(blocks, function(block) {
					block.selectable = true;
					block.callback = function(node) {
						 $location.path("/blocks/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: blocks, title: "Blocks", route: "/blocks"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all configurations
		 */
		configurations: function() {
			var deferred = $q.defer();
			configurationResourceService.configuration.findAll(function(configurations) {
				_.each(configurations, function(configuration) {
					configuration.selectable = true;
					configuration.callback = function(node) {
						 $location.path("/configurations/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: configurations, title: "Configurations", route: "/configurations"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all properties
		 */
		properties: function() {
			var deferred = $q.defer();
			propertyResourceService.property.findAll(function(properties) {
				_.each(properties, function(property) {
					property.selectable = true;
					property.callback = function(node) {
						 $location.path("/properties/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: properties, title: "Properties", route: "/properties"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		views: function() {
			var deferred = $q.defer();
			viewResourceService.view.findAll(function(views) {
				_.each(views, function(view) {
					view.selectable = true;
					view.callback = function(node) {
						 $location.path("/views/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: views, title: "Views", route: "/views"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		triggers: function() {
			var deferred = $q.defer();
			triggerResourceService.trigger.findAll(function(triggers) {
				_.each(triggers, function(trigger) {
					trigger.selectable = true;
					trigger.callback = function(node) {
						 $location.path("/triggers/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: triggers, title: "Triggers", route: "/triggers"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		scenarios: function() {
			var deferred = $q.defer();
			scenarioService.scenario.findAll(function(scenarios) {
				_.each(scenarios, function(scenario) {
					scenario.selectable = true;
					scenario.callback = function(node) {
						 $location.path("/scenarios/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: scenarios, title: "Scenarios", route: "/scenarios"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all crons
		 */
		crons: function() {
			var deferred = $q.defer();
			cronResourceService.cron.findAll(function(crons) {
				_.each(crons, function(cron) {
					cron.selectable = true;
					cron.callback = function(node) {
						 $location.path("/crons/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: crons, title: "Crontab", route: "/crons"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all crons
		 */
		connectors: function() {
			var deferred = $q.defer();
			connectorResourceService.connector.findAll(function(connectors) {
				_.each(connectors, function(connector) {
					connector.selectable = true;
					connector.callback = function(node) {
						 $location.path("/connectors/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: connectors, title: "Connectors", route: "/connectors"});
	    	});
	    	return deferred.promise;
		}
	}
}])
.directive('jarvisWidgetNavigator', [
             '$log', '$location', '$stateParams', 'jarvisWidgetNavigatorService',
             function ($log, $location, $stateParams, componentService) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/navigator/jarvis-widget-navigator.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNavigator', [
             '$log', '$location', '$stateParams', 'jarvisWidgetNavigatorService',
             function ($log, $location, $stateParams, componentService) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/navigator/partials/jarvis-navigator.html',
    link: function(scope, element, attrs) {
		scope.elements = [];
		/**
		 * load devices
		 */
		var promise = null;
		if(attrs['resource'] === 'devices') {
			promise = componentService.devices();
		}
		if(attrs['resource'] === 'plugins') {
			promise = componentService.plugins();
		}
		if(attrs['resource'] === 'commands') {
			promise = componentService.commands();
		}
		if(attrs['resource'] === 'snapshots') {
			promise = componentService.snapshots();
		}
		if(attrs['resource'] === 'blocks') {
			promise = componentService.blocks();
		}
		if(attrs['resource'] === 'configurations') {
			promise = componentService.configurations();
		}
		if(attrs['resource'] === 'properties') {
			promise = componentService.properties();
		}
		if(attrs['resource'] === 'views') {
			promise = componentService.views();
		}
		if(attrs['resource'] === 'triggers') {
			promise = componentService.triggers();
		}
		if(attrs['resource'] === 'scenarios') {
			promise = componentService.scenarios();
		}
		if(attrs['resource'] === 'crons') {
			promise = componentService.crons();
		}
		if(attrs['resource'] === 'connectors') {
			promise = componentService.connectors();
		}
		if(promise != null) {
			promise.then(function(result){
	    		scope.elements.push({
			    	 name:result.title,
			    	 selectable : true,
					 callback : function(node) {
						 $location.path(result.route);
					 },
			    	 nodes:result.elements
			     });
	    	});
		}
    }
  }
}]);
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.notification', ['JarvisApp.services'])
.controller('notificationsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetNotificationService',
		function($scope, $log, genericScopeService, notificationResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.notifications = entities;
			},
			function() {
				return $scope.notifications;
			},
			$scope, 
			'notifications', 
			notificationResourceService.notification,
			{
    			name: "notification name",
    			icon: "settings_remote"
    		}
	);
}])
.controller('notificationCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'genericPickerService', 'jarvisWidgetNotificationService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, genericPickerService, notificationResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'notification', 
			'notifications', 
			notificationResourceService.notification
	);
    /**
     * sending a simple test
     */
    $scope.test = function(notification, text, title, subtext) {
    	notificationResourceService.notification.task(notification.id, 'test', {'text': text, 'title': title, 'subtext': subtext}, function(data) {
   	    	toastService.info('notification ' + notification.name + '#' + notification.id + ' tested');
	    }, toastService.failure);
    }
    /**
     * loading
     */
    $scope.load = function() {
		$scope.combo = {
				types: notificationResourceService.types
		}

		/**
		 * get current notification
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.notification=update}, notificationResourceService.notification);
    	$log.info('notification-ctrl', $scope.notification);
    }
}])
.factory('jarvisWidgetNotificationService', [ 'genericResourceService', function( genericResourceService) {
	return {
		notification: genericResourceService.crud(['notifications']),
	  	types: [
	           	   {
	          		   id: 'SLACK',
	          		   value:'notification.slack'
	          	   },
	          	   {
	          		   id: 'MAIL',
	          		   value:'notification.mail'
	          	   }
	        ]
	}
}])
.directive('jarvisWidgetNotifications', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/jarvis-widget-notifications.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisWidgetNotification', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/jarvis-widget-notification.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNotifications', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notifications.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNotification', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notification-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNotificationTest', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notification-test.html',
    link: function(scope, element, attrs) {
    }
  }
}]);
/* 
 * Copyright 2016 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.plugin', ['JarvisApp.services'])
.controller('pluginsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetPluginService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.plugins = entities;
			},
			function() {
				return $scope.plugins;
			},
			$scope, 
			'plugins/scripts', 
			componentService.scripts,
			{
    			name: "script name",
    			icon: "list",
       			type: "script"
    		}
	);
}])
.controller('pluginScriptCtrl',
		[ '$scope',
		  '$log',
		  '$stateParams',
		  'genericResourceService',
		  'genericScopeService',
		  'genericPickerService',
		  'jarvisWidgetPluginService',
		  'jarvisWidgetDeviceService',
		  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			genericResourceService,
			genericScopeService,
			genericPickerService,
			componentService,
			deviceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'script', 
			'plugins', 
			componentService.scripts
	);
	/**
	 * declare links
	 */
	$scope.links = {
			commands: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.commands;
			},
			$scope.links.commands, 
			'script', 
			'plugins', 
			componentService.scripts, 
			componentService.commands, 
			{
    			'order':'1',
    			'name':'noname',
       			'nature':'json',
       			'type':'data'
			},
			$stateParams.id
	);
    /**
     * execute these command on server side (only action command)
	 * @param command, the command to be executed
     */
    $scope.execute = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
    		componentService.scripts.task(command.id, 'execute', $scope.rawTestData, function(data) {
       	    	$log.debug('pluginScriptCtrl::execute', command, data);
       	    	$scope.rawoutput = data;
       	    	$scope.output = angular.toJson(data, true);
    	    }, toastService.failure);
    	}
    }
    /**
     * render these command on server side (only data command)
	 * @param command, the command to be executed
     */
    $scope.render = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
    		componentService.scripts.task(command.id, 'render', $scope.rawTestData, function(data) {
       	    	$log.debug('pluginScriptCtrl::render', command, data);
       	    	$scope.rawoutput = data;
       	    	$scope.output = angular.toJson(data, true);
    	    }, toastService.failure);
    	}
    }
    /**
     * fix params
     */
    $scope.submit = function() {
    	$scope.rawTestData = angular.fromJson($scope.editTestData);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * clear params
     */
    $scope.clear = function() {
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    	$scope.rawTestData = {};
    	$scope.editTestData = angular.toJson($scope.rawTestData, true);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * set owner
     */
    $scope.setOwner = function(owner) {
    	$scope.script.owner = owner.id;
    }
    /**
     * load controller
     */
    $scope.load = function() {
    	$scope.activeTab = $stateParams.tab;
    	
    	$scope.clear();
    	
	    /**
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true, value:'True'},
		               	   {id: false,value:'False'}
		        ]
		};
	
		/**
		 * get current script
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.script=update}, componentService.scripts);
	
		/**
		 * get all commands
		 */
    	$scope.commands = [];
    	genericResourceService.scope.collections.findAll('commands', $stateParams.id, $scope.commands, componentService.commands);

		/**
		 * find all owner
		 */
    	$scope.combo.owners = [{id: undefined, name: "device.empty"}];
    	genericResourceService.scope.combo.findAll('owner', $scope.combo.owners, deviceService.device);

		$log.info('script-ctrl', $scope.script);
    }
}])
.factory('jarvisWidgetPluginService', 
		[ 'Restangular', 'filterService', 'genericResourceService', 
		  function(Restangular, filterService, genericResourceService) {
	  var plugins = {
		        /**
				 * base services : findAll, delete, put and post
				 */
				findAll: function(callback, failure) {
	                var arr = [];
	                var plugins = ['scripts'];
					var done = _.after(plugins.length, function(loaded) {
						callback(loaded);
					});
					_.forEach(plugins, function(plugin) {
						Restangular.all('plugins').all(plugin).getList().then(function(elements) {
			            	_.forEach(elements, function(element) {
			                    arr.push(filterService.plain(element));
			                });
			            	done(arr);
						},function(errors){
							failure(errors);
						});
					});
				}
	  };
	  return {
		    plugins: plugins,
		    scripts: genericResourceService.crud(['plugins','scripts']),
		    commands : genericResourceService.links(['plugins','scripts'], ['commands'])
	  }
}])
/**
 * plugins
 */
.directive('jarvisWidgetPlugins', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/jarvis-widget-plugins.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisPlugins', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/partials/jarvis-plugins.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * plugin
 */
.directive('jarvisWidgetPlugin', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/jarvis-widget-plugin.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisPluginGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/partials/jarvis-plugin-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisPluginResult', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/partials/jarvis-plugin-result.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisPluginScript', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/plugin/partials/jarvis-plugin-script.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.property', ['JarvisApp.services'])
.controller('propertiesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetPropertyService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.properties = entities;
			},
			function() {
				return $scope.properties;
			},
			$scope, 
			'properties', 
			componentService.property,
			{
    			key: "key",
    			value: "value"
    		}
	);
}])
.controller('propertyCtrl',
		[ '$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'jarvisWidgetPropertyService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'property', 
			'properties', 
			componentService.property);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current property
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.property=update;
    	}, componentService.property);

    	$log.info('property-ctrl', $scope.properties);
    }
}])
.factory('jarvisWidgetPropertyService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		  property : genericResourceService.crud(['properties'])
	  }
}])
/**
 * properties
 */
.directive('jarvisWidgetProperties', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/jarvis-widget-properties.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisProperties', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/partials/jarvis-properties.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * property
 */
.directive('jarvisWidgetProperty', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/jarvis-widget-property.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisProperty', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/partials/jarvis-property-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.scenario', ['JarvisApp.services'])
.controller('scenariosCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetScenarioService', 'toastService',
	function($scope, $log, genericScopeService, jarvisWidgetScenarioService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.scenarios = entities;
			},
			function() {
				return $scope.scenarios;
			},
			$scope, 
			'scenarios', 
			jarvisWidgetScenarioService.scenario,
			{
    			name: "scenario name",
    			icon: "list"
    		}
	);
}])
.controller('scenarioCtrl',
		[ '$scope',
		  '$log',
		  '$stateParams',
		  '$mdDialog',
		  'genericScopeService',
		  'genericResourceService',
		  'genericPickerService',
		  'jarvisWidgetScenarioService',
		  'jarvisWidgetBlockService',
		  'jarvisWidgetPluginService',
		  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			$mdDialog,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			jarvisWidgetScenarioService,
			blockResourceService,
			pluginResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'scenario', 
			'scenarios', 
			jarvisWidgetScenarioService.scenario);
	/**
	 * declare links
	 */
	$scope.links = {
			blocks: {},
			triggers: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks;
			},
			$scope.links.blocks,
			'scenario', 
			'scenarios', 
			jarvisWidgetScenarioService.scenario,
			jarvisWidgetScenarioService.blocks, 
			{
				'order':'1'
			},
			$stateParams.id
	);
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'scenario', 
			'scenarios', 
			jarvisWidgetScenarioService.scenario,
			jarvisWidgetScenarioService.triggers, 
			{
				'order':'1'
			},
			$stateParams.id
	);
    /**
     * render scenario
     */
    $scope.build = function(resource) {
    	var codes = [];
    	_.each(resource, function(graph) {
    		$log.debug("graph", graph.stage);
        	var code = "";
	    	_.each(graph.nodes, function(node) {
	    		if(node.start) {
	    			code += '#' + node.id + '=>start: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.end) {
	    			code += '#' + node.id + '=>end: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.activity) {
	    			code += '#' + node.id + '=>operation: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.gateway) {
	    			code += '#' + node.id + '=>condition: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.call) {
	    			code += '#' + node.id + '=>subroutine: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    	});
	    	_.each(graph.edges, function(edge) {
	    		if(graph.nodes[edge.sourceId].gateway) {
	        		if(edge.bool) {
	        			code += '#' + edge.sourceId + '(yes, right)->#' + edge.targetId + '\n';
	        		} else {
	        			code += '#' + edge.sourceId + '(no)->#' + edge.targetId + '\n';
	        		}
	    		} else {
	    			code += '#' + edge.sourceId + '->#' + edge.targetId + '\n';
	    		}
	    	});
        	codes.push({"stage":graph.stage, "code": code});
    	});
    	return codes;
    }
    /**
     * render each graph
     */
    $scope.render = function(resources) {
		$log.debug(resources);
    	$('#canvas').html('');
    	_.each(resources, function(result) {
        	$('#canvas').append('<h4>'+result.stage+'</h4>');
        	$('#canvas').append('<div id="canvas-'+result.stage+'"></div>');
    	});
    	_.each(resources, function(result) {
    		$log.info("Render", result.stage);
    		var stage = result.stage;
 	    	var chart = flowchart.parse(result.code);
	        chart.drawSVG('canvas-'+result.stage, {
	          // 'x': 30,
	          // 'y': 50,
	          'line-width': 3,
	          'line-length': 50,
	          'text-margin': 10,
	          'font-size': 12,
	          'font': 'normal',
	          'font-family': 'Helvetica',
	          'font-weight': 'normal',
	          'font-color': 'black',
	          'line-color': 'black',
	          'element-color': 'black',
	          'fill': 'white',
	          'yes-text': 'yes',
	          'no-text': 'no',
	          'arrow-end': 'block',
	          'scale': 1,
	          'symbols': {
	            'start': {
	              'font-color': 'green',
	              'element-color': 'green',
	              'fill': 'yellow'
	            },
	            'end':{
	              'font-color': 'red',
	              'element-color': 'red',
	              'fill': 'yellow'
	            }
	          },
	          'flowstate' : {
	            'past' : { 'fill' : '#CCCCCC', 'font-size' : 12},
	            'current' : {'fill' : 'yellow', 'font-color' : 'red', 'font-weight' : 'bold'},
	            'future' : { 'fill' : '#FFFF99'},
	            'request' : { 'fill' : 'blue'},
	            'invalid': {'fill' : '#444444'},
	            'approved' : { 'fill' : '#58C4A3', 'font-size' : 12, 'yes-text' : 'APPROVED', 'no-text' : 'n/a' },
	            'rejected' : { 'fill' : '#C45879', 'font-size' : 12, 'yes-text' : 'n/a', 'no-text' : 'REJECTED' }
	          }
	        });
    	});
    }
    /**
     * load this controller
     */
    $scope.chart = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		jarvisWidgetScenarioService.scenario.task(scenario.id, 'render', {}, function(data) {
       	    	$log.debug('[SCENARIO/render]', scenario, data);
       	    	$scope.codes = $scope.build(data);
       	    	$scope.render($scope.codes);
    	    }, toastService.failure);
    	}
    }
    /**
     * execute this scenario
	 * @param scenario, the scenario to be executed
     */
    $scope.execute = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		jarvisWidgetScenarioService.scenario.task(scenario.id, 'execute', {}, function(data) {
       	    	$log.debug('[SCENARIO/execute]', scenario, data);
       	    	$scope.console = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
  		$scope.plugin = {};

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.scenario=update;
    		$scope.chart(update);
    	}, jarvisWidgetScenarioService.scenario);

		/**
		 * find all blocks
		 */
    	$scope.blocks = [];
    	genericResourceService.scope.collections.findAll(
    			'blocks', $stateParams.id, $scope.blocks, jarvisWidgetScenarioService.blocks,
    			function(blocks) {
    				_.forEach(blocks, function(block) {
    					block.plugins = {};
    					block.plugins.if   = [];
    					block.plugins.then = [];
    					block.plugins.else = [];
    					blockResourceService.plugins.if.findAll(block.id, function(data){
    						block.plugins.if = data;
    					});
    					blockResourceService.plugins.then.findAll(block.id, function(data){
    						block.plugins.then = data;
    					});
    					blockResourceService.plugins.else.findAll(block.id, function(data){
    						block.plugins.else = data;
    					});
    					block.blocks = {};
    					block.blocks.then = [];
    					block.blocks.else = [];
    					blockResourceService.blocks.then.findAll(block.id, function(data){
    						block.blocks.then = data;
    					});
    					blockResourceService.blocks.else.findAll(block.id, function(data){
    						block.blocks.else = data;
    					});
    					$log.debug("blocks.plugins", block.plugins);
    					$log.debug("blocks.blocks", block.blocks);
    				});
    			}
    	);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, jarvisWidgetScenarioService.triggers);

		$log.debug('scenario-ctrl', $scope.scenario);
    }
}])
.factory('jarvisWidgetScenarioService', [ 'genericResourceService', function(genericResourceService) {
	return {
		   scenario : genericResourceService.crud(['scenarios']),
		   blocks  : genericResourceService.links(['scenarios'], ['blocks']),
		   triggers: genericResourceService.links(['scenarios'], ['triggers'])
	}
}])
/**
 * scenarios
 */
.directive('jarvisWidgetScenarios', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/jarvis-widget-scenarios.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenarios', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenarios.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * cron
 */
.directive('jarvisWidgetScenario', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/jarvis-widget-scenario.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenario', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenario-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenarioBlock', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenario-block.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenarioBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenario-blocks.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenarioConsole', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenario-console.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisScenarioGraph', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/scenario/partials/jarvis-scenario-graph.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.snapshot', ['JarvisApp.services','JarvisApp.directives.files'])
.controller('snapshotsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetSnapshotService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.snapshots = entities;
			},
			function() {
				return $scope.snapshots;
			},
			$scope, 
			'snapshots', 
			componentService.snapshot,
			{
    			name: "snapshot name",
    			icon: "list"
    		}
	);
}])
.controller('snapshotCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'jarvisWidgetSnapshotService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'snapshot', 
			'snapshots', 
			componentService.snapshot);
    /**
     * restore configuration with this snapshot
	 * @param snapshot, the snapshot
     */
    $scope.restore = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		componentService.snapshot.task(snapshot.id, 'restore', {}, function(data) {
       	    	$log.debug('snapshotCtrl::restore', snapshot, data);
    	    }, toastService.failure);
    	}
    }
    /**
     * download current snapshot
	 * @param snapshot, the snapshot to be downloaded
     */
    $scope.download = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		componentService.snapshot.task(snapshot.id, 'download', {}, function(data) {
    			var fileName = 'export-'+$filter('date')(new Date(), 'yyyyMMdd-HHmmss') + '.json';
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
       	    	$log.debug('snapshot', snapshot, data);
       	    	var file = new Blob([angular.toJson(data, true)], {type: 'application/text'});
                var fileURL = window.URL.createObjectURL(file);
                a.href = fileURL;
                a.download = fileName;
                a.click();
    	    }, toastService.failure);
    	}
    }
    /**
     * load local file
	 * @param id of input file
     */
    $scope.upload = function(id) {
    	$('#'+id).trigger('click');
    }
    /**
     * upload callback
	 * @param snapshot, the snapshot
	 * @param file, data to store in snapshot (on client side)
     */
    $scope.loaded = function(snapshot, file) {
    	snapshot.json = file.data;
    	$log.debug('loaded', snapshot, file);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current snapshot
		 */
    	$scope.snapshots = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.snapshot=update}, componentService.snapshot);

		$log.info('snapshot-ctrl', $scope.snapshot);
    }
}])
.factory('jarvisWidgetSnapshotService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		  snapshot : genericResourceService.crud(['snapshots'])
	  }
}])
/**
 * snapshots
 */
.directive('jarvisWidgetSnapshots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/jarvis-widget-snapshots.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisSnapshots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/partials/jarvis-snapshots.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * device
 */
.directive('jarvisWidgetSnapshot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/jarvis-widget-snapshot.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisSnapshot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/partials/jarvis-snapshot-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.trigger', ['JarvisApp.services'])
.controller('jarvisWidgetTriggersCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetTriggerService',
		function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.triggers = entities;
			},
			function() {
				return $scope.triggers;
			},
			$scope, 
			'triggers', 
			componentService.trigger,
			{
    			name: "trigger name",
    			icon: "settings_remote"
    		}
	);
}])
.controller('jarvisWidgetTriggerCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'genericPickerService', 'jarvisWidgetTriggerService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, genericPickerService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'trigger', 
			'triggers', 
			componentService.trigger
	);
	/**
	 * declare links
	 */
	$scope.links = {
			crons: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.crons;
			},
			$scope.links.crons,
			'cron',
			'crons',
			componentService.trigger, 
			componentService.crons, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * execute this trigger
     */
    $scope.execute = function(trigger) {
    	componentService.trigger.task(trigger.id, 'execute',  {}, function(data) {
   	    	toastService.info('trigger ' + trigger.name + '#' + trigger.id + ' executed');
	    }, toastService.failure);
    }
    /**
     * loading
     */
    $scope.load = function() {
		/**
		 * get current trigger
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.trigger=update}, componentService.trigger);
	
		/**
		 * get all crontabs
		 */
    	$scope.crons = [];
    	genericResourceService.scope.collections.findAll('crons', $stateParams.id, $scope.crons, componentService.crons);

    	$log.info('trigger-ctrl', $scope.trigger);
    }
}])
.factory('jarvisWidgetTriggerService', [ 'genericResourceService', function(genericResourceService) {
	return {
		trigger: genericResourceService.crud(['triggers']),
		crons : genericResourceService.links(['triggers'], ['crons']),
	}
}])
.directive('jarvisWidgetTriggers', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/trigger/jarvis-widget-triggers.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisTriggers', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/trigger/partials/jarvis-triggers.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisWidgetTrigger', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/trigger/jarvis-widget-trigger.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisTriggerGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/trigger/partials/jarvis-trigger-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisTriggerCron', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/trigger/partials/jarvis-trigger-cron.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;
/* 
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/* Ctrls */

angular.module('jarvis.directives.view', ['JarvisApp.services'])
.controller('viewsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetViewService',
	function($scope, $log, genericScopeService, componentService){
    $log.info('viewsCtrl');
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.views = entities;
			},
			function() {
				return $scope.views;
			},
			$scope, 
			'views', 
			componentService.view,
			{
    			name: "view name",
    			icon: "list"
    		}
	);

	$log.info('views-ctrl', $scope.views);
}])
.controller('viewCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'jarvisWidgetViewService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'view', 
			'views', 
			componentService.view);
	/**
	 * declare links
	 */
	$scope.links = {
			devices: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.devices;
			},
			$scope.links.devices, 
			'view', 
			'views', 
			componentService.view, 
			componentService.devices, 
			{
    			'order':'1'
			},
			$stateParams.id
	);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current view
		 */
    	$scope.views = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.view=update}, componentService.view);
	
		/**
		 * get all views
		 */
		$scope.devices = [];
    	genericResourceService.scope.collections.findAll('devices', $stateParams.id, $scope.devices, componentService.devices);

		$log.info('view-ctrl', $scope.views);
    }
}])
.factory('jarvisWidgetViewService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  view : genericResourceService.crud(['views']),
	  devices : genericResourceService.links(['views'], ['devices'])
  }
}])
/**
 * Views
 */
.directive('jarvisWidgetViews', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/jarvis-widget-views.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisViews', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/partials/jarvis-views.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * View
 */
.directive('jarvisWidgetView', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/jarvis-widget-view.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisViewGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/partials/jarvis-view-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

