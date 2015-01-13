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
	$scope.loadConfiguration = function() {
		/**
		 * load configuration elements
		 */
		$scope.jarvis.configuration = {};
		/**
		 * loading properties
		 */
		jarvisServices.getProperties({}, function(data) {
			console.log(data);
			$scope.jarvis.configuration.properties = data.properties;
			/**
			 * refresh jquerymobile widget
			 */
			setTimeout(function() {
				$("#configuration-properties").table("refresh");
				console.log("properties loaded ...");
			}, 100);
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
		/**
		 * loading clients
		 */
		jarvisServices.getClients({}, function(data) {
			console.log(data);
			$scope.jarvis.configuration.clients = data.clients;
			/**
			 * refresh jquerymobile widget
			 */
			setTimeout(function() {
				$("#configuration-clients").table("refresh");
				console.log("clients loaded ...");
			}, 100);
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
		/**
		 * loading events
		 */
		jarvisServices.getEvents({}, function(data) {
			console.log(data);
			$scope.jarvis.configuration.events = data.events;
			/**
			 * refresh jquerymobile widget
			 */
			setTimeout(function() {
				$("#configuration-events").table("refresh");
				console.log("events loaded ...");
			}, 100);
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	/**
	 * load mongodb
	 */
	$scope.loadMongodb = function() {
		/**
		 * load mongodb elements
		 */
		$scope.jarvis.mongodb = {};
		/**
		 * loading mongodb collections
		 */
		jarvisServices.getDbCollections({}, function(data) {
			console.log(data);
			$scope.jarvis.mongodb.collections = data;
			/**
			 * refresh jquerymobile widget
			 */
			setTimeout(function() {
				$("#mongodb-collections").table("refresh");
				console.log("collections loaded ...");
			}, 100);
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
