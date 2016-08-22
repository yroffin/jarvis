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

angular.module('jarvis.directives.connector', ['JarvisApp.services'])
.controller('connectorsCtrl', 
		['$scope', '$log', 'genericScopeService', 'jarvisWidgetConnectorService',
	function($scope, $log, genericScopeService, connectorResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.connectors = entities;
			},
			function() {
				return $scope.connectors;
			},
			$scope, 
			'connectors', 
			connectorResourceService.connector,
			{
    			name: "connector name",
    			icon: "settings_input_antenna",
    			isRenderer: false,
    			isSensor: false,
    			canAnswer: false
    		}
	);
}])
.controller('connectorCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'jarvisWidgetConnectorService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, connectorResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'connector', 
			'connectors', 
			connectorResourceService.connector);
    /**
     * verify this connector
     */
    $scope.ping = function(connector) {
    	connectorResourceService.connector.task(connector.id, 'ping', connector, function(data) {
    		/**
    		 * check data for ping result
    		 */
    		$scope.status = angular.toJson(data, true);
    	}, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current connector
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.connector=update;
    	}, connectorResourceService.connector);

    	$log.info('connector-ctrl', $scope.connectors);
    }
}])
.factory('jarvisWidgetConnectorService', [ 'genericResourceService', function( genericResourceService) {
	return {
		connector: genericResourceService.crud(['connectors'])
	}
}])
/**
 * commands
 */
.directive('jarvisWidgetConnectors', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/jarvis-widget-connectors.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConnectors', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/partials/jarvis-connectors.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * command
 */
.directive('jarvisWidgetConnector', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/jarvis-widget-connector.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisConnectorGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/connector/partials/jarvis-connector-general.html',
    link: function(scope, element, attrs) {
    }
  }
}]);
