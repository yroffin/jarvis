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
		$scope.jarvis.newjob = {};
		$scope.jarvis.configuration = {};
	}
	/**
	 * load configuration
	 */
	$scope.loadConfiguration = function(target) {
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
				 * loading jobs
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
	 * load neo4jdb
	 */
	$scope.loadNeo4jdb = function(target) {
		/**
		 * load neo4jdb elements
		 */
		$scope.jarvis.neo4jdb = {};
		/**
		 * loading neo4jdb collections then navigate to target
		 */
		jarvisServices.getDbCollections({}, function(data) {
			$scope.jarvis.neo4jdb.collections = data;
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
		var offset = collection.count - 20;
		if(offset < 0) {
			offset = 0;
		}
		/**
		 * loading neo4jdb collections then navigate to target
		 */
		jarvisServices.getCollection({
			'database' : collection.db,
			'name' : collection.name,
			'offset' : offset,
			'page' : 20
		}, function(data) {
			var columns = [];
			for ( var column in data[0]) {
				if (column.indexOf('$') == -1 && column.indexOf('toJSON') == -1) {
					columns.push(column);
				}
			}
			$scope.jarvis.neo4jdb.current = collection;
			$scope.jarvis.neo4jdb.collection = data;
			$scope.jarvis.neo4jdb.columns = columns;
			$scope.jarvis.neo4jdb.offset = offset;
			$scope.jarvis.neo4jdb.page = 20;

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
	 * send a message to this renderer
	 */
	$scope.createJob = function(target) {
		var job = $scope.jarvis.newjob;
		console.info(job);
		/**
		 * loading jobs
		 */
		jarvisServices.createJob({
			job : job.jobname,
			plugin : job.plugin,
			cronTime : job.cron,
			params : job.args
		}, function(data) {
			/**
			 * loading jobs
			 */
			jarvisServices.getJobs({}, function(data) {
				console.warn('jobs', target, data);
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
		});
	}
	/**
	 * send a message to this renderer
	 */
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
	$scope.testJob = function(target) {
		/**
		 * loading clients
		 */
		jarvisServices.testJob({
			plugin : target.plugin,
			job : target.job
		}, function(data) {
			console.log(data);
		}, function(failure) {
			/**
			 * TODO : handle error message
			 */
		});
	}
	$scope.deleteJobByName = function(target) {
		/**
		 * loading clients
		 */
		jarvisServices.deleteJobByName({
			name : target.job
		}, function(data) {
			/**
			 * loading jobs
			 */
			jarvisServices.getJobs({}, function(data) {
				console.warn('jobs updated', data);
				$scope.jarvis.configuration.jobs = data;
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
	$scope.selectItem = function(item) {
	}
	$scope.selectGroup = function(item) {
	}
} ]);
