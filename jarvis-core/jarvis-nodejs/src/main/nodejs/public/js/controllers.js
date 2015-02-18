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

angular.module('myApp.controllers', []).controller('BootstrapCtrl', [ '$rootScope', '$window', 'jarvisServices', function($scope, $window, jarvisServices) {
	/**
	 * bootstrap
	 */
	$scope.load = function() {
		$scope.theme = "b";
		$scope.inventoryItem = {
			href : ''
		};
		$scope.jarvis = {};
	}
	/**
	 * load configuration
	 */
	$scope.loadConfiguration = function(target) {
		/**
		 * load configuration elements
		 */
		$scope.jarvis.configuration = {};
		/**
		 * loading properties
		 */
		jarvisServices.getProperties({}, function(data) {
			/**
			 * deprecated
			 */
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
		/**
		 * loading clients
		 */
		jarvisServices.getClients({}, function(data) {
			$scope.jarvis.configuration.clients = data.clients;
			console.warn('client', data);
			/**
			 * loading events
			 */
			jarvisServices.getEvents({}, function(data) {
				console.warn('event', target);
				$scope.jarvis.configuration.events = data.events;

				/**
				 * loading events
				 */
				jarvisServices.getJobs({}, function(data) {
					console.warn('jobs', target);
					$scope.jarvis.configuration.jobs = data;
					/**
					 * navigate to target
					 */
					$.mobile.navigate(target, {
						info : "navigate to " + target
					});
				}, function(failure) {
					/**
					 * TODO : handle error message
					 */
				});
			}, function(failure) {
				/**
				 * TODO : handle error message
				 */
			});
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	/**
	 * load mongodb
	 */
	$scope.loadMongodb = function(target) {
		/**
		 * load mongodb elements
		 */
		$scope.jarvis.mongodb = {};
		/**
		 * loading mongodb collections then navigate to target
		 */
		jarvisServices.getDbCollections({}, function(data) {
			$scope.jarvis.mongodb.collections = data;
			/**
			 * navigate to target
			 */
			$.mobile.navigate(target, {
				info : "navigate to " + target
			});
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	/**
	 * load collection in current scope
	 * 
	 * @param collection
	 *            the selected collection
	 */
	$scope.loadCollection = function(collection, target) {
		/**
		 * loading mongodb collections then navigate to target
		 */
		jarvisServices.getCollection({
			'database' : collection.db,
			'name' : collection.name,
			'offset' : collection.count - 20,
			'page' : 20
		}, function(data) {
			var columns = [];
			for ( var column in data[0]) {
				if (column.indexOf('$') == -1 && column.indexOf('toJSON') == -1) {
					columns.push(column);
				}
			}
			$scope.jarvis.mongodb.current = collection;
			$scope.jarvis.mongodb.collection = data;
			$scope.jarvis.mongodb.columns = columns;
			$scope.jarvis.mongodb.offset = collection.count - 20;
			$scope.jarvis.mongodb.page = 20;

			/**
			 * navigate to target
			 */
			$.mobile.navigate(target, {
				info : "navigate to " + target
			});
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	$scope.send = function(target, element) {
		/**
		 * loading clients
		 */
		jarvisServices.send({
			target : target,
			message : element
		}, function(data) {
			console.log(data);
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	$scope.loadGroups = function() {
	}
	$scope.selectItem = function(item) {
	}
	$scope.selectGroup = function(item) {
	}
} ]);
