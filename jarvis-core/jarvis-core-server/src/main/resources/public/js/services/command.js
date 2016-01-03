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
var myAppServices = angular.module('JarvisApp.services.command', []);

/**
 * iotResourceService
 */
myAppServices.factory('commandResourceService', function($log, Restangular, filterService) {
  var api = 'commands';
  var base = {
        /**
		 * base services : findAll, delete, put and post
		 */
		findAll: function(callback, failure) {
			Restangular.all(api).getList().then(function(elements) {
                var arr = [];
            	_.forEach(elements, function(element) {
                    arr.push(filterService.command(element));
                });
				callback(arr);
			},function(errors){
				failure(errors);
			});
		},
		get: function(id, callback, failure) {
			Restangular.one(api, id).get().then(function(element) {
				callback(filterService.command(element));
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
			Restangular.one(api, element.id).customPUT(element).then(function(command) {
				callback(filterService.command(command));
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
	    base: base,
		ext : ext  
  }
});
