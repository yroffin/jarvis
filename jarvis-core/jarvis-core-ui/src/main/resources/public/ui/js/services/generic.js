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
