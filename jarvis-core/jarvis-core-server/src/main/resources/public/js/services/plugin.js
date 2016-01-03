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
var myAppServices = angular.module('JarvisApp.services.plugin', []);

/**
 * iotResourceService
 */
myAppServices.factory('pluginResourceService', function($log, Restangular, filterService, commandResourceService) {
  var api = 'plugins';
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
					Restangular.all(api).all(plugin).getList().then(function(elements) {
		            	_.forEach(elements, function(element) {
		            		$log.debug(filterService.plugin(element));
		                    arr.push(filterService.plugin(element));
		                });
		            	done(arr);
					},function(errors){
						failure(errors);
					});
				});
			}
  };
  var scripts = {
        /**
		 * base services : findAll, delete, put and post
		 */
		findAll: function(callback, failure) {
			Restangular.all(api).all('scripts').getList().then(function(elements) {
                var arr = [];
            	_.forEach(elements, function(element) {
                    arr.push(filterService.script(element));
                });
				callback(arr);
			},function(errors){
				failure(errors);
			});
		},
		get: function(id, callback, failure) {
			Restangular.all(api).one('scripts', id).get().then(function(element) {
				callback(filterService.script(element));
			},function(errors){
				failure(errors);
			});
		},
		delete: function(id, callback, failure) {
			Restangular.all(api).one('scripts', id).remove().then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		},
		put: function(element, callback, failure) {
			Restangular.all(api).one('scripts', element.id).customPUT(element).then(function(jobs) {
				callback(jobs);
			},function(errors){
				failure(errors);
			});
		},
		post: function(element, callback, failure) {
			Restangular.all(api).all('scripts').post(element).then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		}
  };
  var commands = {
		findAll: function(id, callback, failure) {
			Restangular.all(api).one('scripts', id).all('commands').getList().then(function(elements) {
				var done = _.after(elements.length, function(params) {
					callback(params);
				});
				var params = [];
            	_.forEach(elements, function(element) {
            		commandResourceService.base.get(element.id, function(param) {
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
        put: function(id, param, callback, failure) {
        	Restangular.all(api).one('scripts', id).one('commands',param).customPUT({}).then(function(href) {
        		callback(href);
        	},function(errors){
        		failure(errors);
        	});
        },
        /**
		 * delete link
		 */
		delete: function(id, param, instance, callback, failure) {
			Restangular.all(api).one('scripts', id).one('commands', param).remove({'instance':instance}).then(function(href) {
				callback(href);
			},function(errors){
				failure(errors);
			});
		}
  }
  return {
	    plugins: plugins,
	    scripts: scripts,
		commands : commands  
  }
});
