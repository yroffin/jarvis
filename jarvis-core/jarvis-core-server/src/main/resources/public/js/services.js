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

myAppServices.factory('jarvisServices', [ '$resource', function($resource, $windows) {
	return $resource('', {}, {
		getProperties: {
			method: 'GET',
			url: jarvisServicesUrl + '/info/properties',
			params: {},
			isArray: false,
			cache: false
		},
		getClients: {
			method: 'GET',
			url: jarvisServicesUrl + '/info/clients',
			params: {},
			isArray: false,
			cache: false
		},
		getEvents: {
			method: 'GET',
			url: jarvisServicesUrl + '/info/events',
			params: {},
			isArray: false,
			cache: false
		},
		send: {
			method: 'GET',
			url: jarvisServicesUrl + '/send',
			params: {},
			isArray: false,
			cache: false
		},
		/**
		 * retrieve neo4j data
		 */
		getDbCollections: {
			method: 'GET',
			url: jarvisServicesUrl + '/neo4j/collections',
			params: {},
			isArray: true,
			cache: false
		},
		/**
		 * count collection tupple
		 */
		countDbCollections: {
			method: 'GET',
			url: jarvisServicesUrl + '/neo4j/crud/:name/count',
			params: {},
			isArray: false,
			cache: false
		},
		/**
		 * count collection tupple
		 */
		getCollection: {
			method: 'GET',
			url: jarvisServicesUrl + '/neo4j/crud/:name/page?offset=:offset&page=:page',
			params: {},
			isArray: true,
			cache: false
		}
	})
}]);

/**
 * connectors services
 */
myAppServices.factory('jarvisConnectorsResource', [ '$resource', function($resource, $windows) {
	return $resource('', {}, {
			/**
			 * get all crontab (raw configuration resource)
			 */
			get: {
				method: 'GET',
				url: jarvisApiUrl + '/connectors',
				params: {},
				isArray: true,
				cache: false
			},
			/**
			 * get label detail (raw configuration resource)
			 */
			byId: {
				method: 'GET',
				url: jarvisApiUrl + '/connector/:id',
				isArray: false,
				cache: false
			}
		}
	)}]);

/**
 * configuration services - /configuration/crontabs - /configuration/neo4j
 */
myAppServices.factory('jarvisConfigurationResource', [ '$resource', function($resource, $windows) {
	return $resource('', {}, {
			/**
			 * get all crontab (raw configuration resource)
			 */
			crontabs: {
				method: 'GET',
				url: jarvisApiUrl + '/configurations/crontabs',
				params: {},
				isArray: true,
				cache: false
			},
			/**
			 * get all labels (raw configuration resource)
			 */
			labels: {
				method: 'GET',
				url: jarvisApiUrl + '/configurations/neo4j/labels',
				params: {},
				isArray: true,
				cache: false
			},
			/**
			 * get label detail (raw configuration resource)
			 */
			label: {
				method: 'GET',
				url: jarvisApiUrl + '/configurations/neo4j/labels/:id?limit=:limit',
				isArray: true,
				cache: false
			}
		}
	)}]);

/**
 * toastService
 */
myAppServices.factory('toastService', function($log, $mdToast) {
  var toastServiceInstance;
  toastServiceInstance = {
		toastPosition: {
		          bottom: false,
		          top: true,
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
});

/**
 * clientResourceService
 */
myAppServices.factory('clientResourceService', function($q, $window, $rootScope, Restangular) {
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('clients').getList().then(function(clients) {
        		callback(clients);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
});

/**
 * crontabResourceService
 */
myAppServices.factory('crontabResourceService', function($q, $window, $rootScope, Restangular) {
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('crontabs').getList().then(function(crontabs) {
        	  callback(crontabs);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
});

/**
 * labelResourceService
 */
myAppServices.factory('labelResourceService', function($q, $window, $rootScope, Restangular) {
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('labels').getList().then(function(labels) {
        	  callback(labels);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
});

