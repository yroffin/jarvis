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

angular.module('JarvisApp.ctrl.configurations', ['JarvisApp.services'])
.controller('configurationsCtrl', 
		['$scope', '$log', 'genericScopeService', 'configurationResourceService',
	function($scope, $log, genericScopeService, configurationResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.configurations = entities;
			},
			function() {
				return $scope.configurations;
			},
			$scope, 
			'configurations', 
			configurationResourceService.configuration,
			{
    			name: "default",
    			opacity: "1",
    			backgroundUrl: "http://artroyalephotography.com/wp-content/uploads/2011/08/minimal-gray-to-white-gradient-wallpapers_33797_1920x1200-1024x640.jpg"
    		}
	);
}])
.controller('configurationCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'configurationResourceService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, configurationResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'configuration', 
			'configurations', 
			configurationResourceService.configuration);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current configuration
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.config=update;
    	}, configurationResourceService.configuration);

    	$log.info('configuration-ctrl', $scope.configurations);
    }
}]);
