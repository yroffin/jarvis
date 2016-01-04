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
var myAppServices = angular.module('JarvisApp.services.view', []);

/**
 * viewResourceService
 */
myAppServices.factory('viewResourceService', function($log, Restangular, iotResourceService, filterService) {
  var api = 'views';
  var base = {
        /**
		 * find all elements
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
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
        /**
		 * find one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		get: function(id, callback, failure) {
			Restangular.one(api, id).get().then(function(element) {
				callback(filterService.plain(element));
			},function(errors){
				failure(errors);
			});
		},
        /**
		 * delete one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(id, callback, failure) {
			Restangular.one(api, id).remove().then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		},
        /**
		 * update one element
		 * @param element, previous loaded element
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(element, callback, failure) {
			Restangular.one(api, element.id).customPUT(element).then(function(view) {
				callback(filterService.plain(view));
			},function(errors){
				failure(errors);
			});
		},
        /**
		 * create one element
		 * @param element, element to create
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(element, callback, failure) {
			Restangular.all(api).post(element).then(function(elements) {
				callback(elements);
			},function(errors){
				failure(errors);
			});
		}
  };
  var iots = {
			findAll: function(id, callback, failure) {
				Restangular.all(api).one(id).all('iots').getList().then(function(elements) {
					var iotStatusLoaded = _.after(elements.length, function(iots) {
						callback(iots);
					});
					var iotLoaded = _.after(elements.length, function(iots) {
						var iotsLoaded = [];
		            	_.forEach(iots, function(iot) {
		            		iotResourceService.ext.task(iot.id, 'render', {}, function(iotLoaded) {
		            			iot.render = iotLoaded;
			            		iotsLoaded.push(iot);
			            		iotStatusLoaded(iotsLoaded);
		            		});
		            	});
					});
					var iots = [];
	            	_.forEach(elements, function(element) {
	            		iotResourceService.iot.get(element.id, function(iot) {
	            			iot.instance = element.instance;
	            			iots.push(iot);
	            			iotLoaded(iots);
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
	        	Restangular.all(api).one(id).one('iots',param).customPUT({}).then(function(href) {
	        		callback(href);
	        	},function(errors){
	        		failure(errors);
	        	});
	        },
	        /**
			 * delete link
			 */
			delete: function(id, param, instance, callback, failure) {
				Restangular.all(api).one(id).one('iots', param).remove({'instance':instance}).then(function(href) {
					callback(href);
				},function(errors){
					failure(errors);
				});
			}
  }
  var ext = {
  }
  return {
	    base: base,
	    iots: iots,
		ext : ext  
  }
});
