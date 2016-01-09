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
 * scenarioResourceService
 */
myAppServices.factory('scenarioResourceService', function($log, Restangular, iotResourceService, filterService) {
  var api = 'scenarios';
  var scenario = {
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
			Restangular.one(api, element.id).customPUT(element).then(function(scenario) {
				callback(filterService.plain(scenario));
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
  var blocks = {
  }
  return {
	    scenario: scenario,
	    blocks: blocks
  }
});
