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

/* Ctrls */

angular.module('JarvisApp.ctrl.events', ['JarvisApp.services'])
.controller('eventsCtrl', 
		['$scope', '$log', 'genericScopeService', 'genericResourceService', 'eventResourceService',
	function($scope, $log, genericScopeService, genericResourceService, eventResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.events = entities;
			},
			function() {
				return $scope.events;
			},
			$scope, 
			'events',
			eventResourceService.event
	);
	/**
	 * some crud
	 */
	$scope.crud = genericResourceService.crud(['events']);
}]);