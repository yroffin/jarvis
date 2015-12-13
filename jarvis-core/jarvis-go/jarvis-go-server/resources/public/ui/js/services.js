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
 * configuration services
 * - /configuration/crontabs
 * - /configuration/neo4j
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

myAppServices.factory('jarvisJobsResource', [ '$resource', function($resource, $windows) {
	return $resource('', {}, {
		/**
		 * create a new crontab entry
		 */
		put : {
			method : 'PUT',
			url : jarvisApiUrl + '/jobs/:id',
			isArray : false,
			cache : false
		},
		/**
		 * get all crontab
		 */
		get : {
			method : 'GET',
			url : jarvisApiUrl + '/jobs',
			params : {},
			isArray : true,
			cache : false
		},
		/**
		 * test current selected job
		 */
		delete : {
			method : 'DELETE',
			url : jarvisApiUrl + '/jobs/:id',
			params : {},
			isArray : false,
			cache : false
		},
		/**
		 * post current selected job
		 */
		post : {
			method : 'POST',
			url : jarvisApiUrl + '/jobs',
			params : {},
			isArray : false,
			cache : false
		},
		/**
		 * test current selected job
		 */
		patch : {
			method : 'PATCH',
			url : jarvisApiUrl + '/jobs/:id',
			params : {},
			isArray : false,
			cache : false
		},
		/**
		 * test current selected job
		 */
		execute : {
			method : 'POST',
			url : jarvisApiUrl + '/jobs/execute?id=:id&method=:method',
			isArray : false,
			cache : false
		},
		/**
		 * delete by name
		 */
		deleteByName : {
			method : 'DELETE',
			url : jarvisServicesUrl + '/neo4j/crontab?name=:name',
			params : {},
			isArray : false,
			cache : false
		}
	})
}]);
