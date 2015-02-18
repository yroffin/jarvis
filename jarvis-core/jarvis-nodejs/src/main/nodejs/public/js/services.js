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
var myAppServices = angular.module('myApp.services', [ 'ngResource' ]);

myAppServices.factory('jarvisServices', [ '$resource', function($resource, $windows) {
	return $resource('', {}, {
		getProperties : {
			method : 'GET',
			url : jarvisServicesUrl + '/info/properties',
			params : {},
			isArray : false,
			cache : false
		},
		getClients : {
			method : 'GET',
			url : jarvisServicesUrl + '/info/clients',
			params : {},
			isArray : false,
			cache : false
		},
		getEvents : {
			method : 'GET',
			url : jarvisServicesUrl + '/info/events',
			params : {},
			isArray : false,
			cache : false
		},
		send : {
			method : 'GET',
			url : jarvisServicesUrl + '/send',
			params : {},
			isArray : false,
			cache : false
		},
		/**
		 * retrieve mongodb collections
		 */
		getDbCollections : {
			method : 'GET',
			url : jarvisServicesUrl + '/mongodb/collections',
			params : {},
			isArray : true,
			cache : false
		},
		/**
		 * count collection tupple
		 */
		countDbCollections : {
			method : 'GET',
			url : jarvisServicesUrl + '/mongodb/crud/:database/:name/count',
			params : {},
			isArray : false,
			cache : false
		},
		/**
		 * count collection tupple
		 */
		getCollection : {
			method : 'GET',
			url : jarvisServicesUrl + '/mongodb/crud/:database/:name/page?offset=:offset&page=:page',
			params : {},
			isArray : true,
			cache : false
		},
		/**
		 * count collection tupple
		 */
		createJob : {
			method : 'POST',
			url : jarvisServicesUrl + '/mongodb/crontab/:plugin/create?job=:job&params=:params&cronTime=:cronTime',
			params : {},
			isArray : true,
			cache : false
		},
		/**
		 * count collection tupple
		 */
		getJobs : {
			method : 'GET',
			url : jarvisServicesUrl + '/mongodb/crontabs/list',
			params : {},
			isArray : true,
			cache : false
		}
	})
} ]);
