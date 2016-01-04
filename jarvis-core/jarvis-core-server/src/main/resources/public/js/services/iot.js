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

angular.module('JarvisApp.services.iot', ['JarvisApp.services.plugin']).factory('iotResourceService', function(Restangular, pluginResourceService, filterService) {
  var api = 'iots';
  var iot = {
        /**
		 * base services : findAll, delete, put and post
		 */
		findAll: function(callback, failure) {
			Restangular.all(api).getList().then(function(elements) {
                var arr = [];
            	_.forEach(elements, function(element) {
                    arr.push(filterService.plain(element));
                });
				callback(arr);
			},function(errors){
				failure(errors);
			});
		},
		get: function(id, callback, failure) {
			Restangular.one(api, id).get().then(function(element) {
				callback(filterService.plain(element));
			},function(errors){
				failure(errors);
			});
		},
		delete: function(id, callback, failure) {
			Restangular.one(api, id).remove().then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		},
		put: function(element, callback, failure) {
			Restangular.one(api, element.id).customPUT(element).then(function(jobs) {
				callback(jobs);
			},function(errors){
				failure(errors);
			});
		},
		post: function(element, callback, failure) {
			Restangular.all(api).post(element).then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		}
  };
  var iots = {
		findAll: function(owner, callback, failure) {
			Restangular.one(api, owner).all('iots').getList().then(function(elements) {
				var done = _.after(elements.length, function(params) {
					callback(params);
				});
				var params = [];
            	_.forEach(elements, function(element) {
            		iot.get(element.id, function(param) {
            			param.instance = element.instance;
            			params.push(param);
            			done(params);
            		},function(errors){
        				failure(errors);
        			})
            	});
			},function(errors){
				failure(errors);
			});
		},
        /**
		 * put link
		 */
        put: function(owner, child, callback, failure) {
        	Restangular.one(api, owner).one('iots',child).customPUT({}).then(function(href) {
        		callback(href);
        	},function(errors){
        		failure(errors);
        	});
        },
        /**
		 * delete link
		 */
		delete: function(owner, child, instance, callback, failure) {
			Restangular.one(api, owner).one('iots', child).remove({'instance':instance}).then(function(href) {
				callback(href);
			},function(errors){
				failure(errors);
			});
		}
  }
  var plugins = {
			findAll: function(owner, callback, failure) {
				Restangular.one(api, owner).all('plugins').getList().then(function(elements) {
					var done = _.after(elements.length, function(params) {
						callback(params);
					});
					var plugins = [];
	            	_.forEach(elements, function(element) {
	            		pluginResourceService.scripts.get(element.id, function(script) {
	            			script.instance = element.instance;
	            			plugins.push(script);
	            			done(plugins);
	            		},function(errors){
	        				failure(errors);
	        			})
	            	});
				},function(errors){
					failure(errors);
				});
			},
	        /**
			 * put link
			 */
	        put: function(owner, child, callback, failure) {
	        	Restangular.one(api, owner).one('plugins',child).customPUT({}).then(function(href) {
	        		callback(href);
	        	},function(errors){
	        		failure(errors);
	        	});
	        },
	        /**
			 * delete link
			 */
			delete: function(owner, child, instance, callback, failure) {
				Restangular.one(api, owner).one('plugins', child).remove({'instance':instance}).then(function(href) {
					callback(href);
				},function(errors){
					failure(errors);
				});
			}
  }
  var ext = {
		  task: function(id, task, args, callback, failure) {
				Restangular.all(api).one(id).customPOST(args,'', {'task':task}).then(function(element) {
					callback(filterService.plain(element));
				},function(errors){
					failure(errors);
				});
			}
  }
  return {
	  	iot: iot,
	  	plugins : plugins,
	  	iots : iots,
	  	ext : ext  
  }
});
