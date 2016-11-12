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

angular.module('jarvis.directives.property', ['JarvisApp.services'])
.controller('propertiesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetPropertyService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.properties = entities;
			},
			function() {
				return $scope.properties;
			},
			$scope, 
			'properties', 
			componentService.property,
			{
    			key: "key",
    			value: "value"
    		}
	);
}])
.controller('propertyCtrl',
		[ '$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'jarvisWidgetPropertyService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'property', 
			'properties', 
			componentService.property);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current property
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.property=update;
    	}, componentService.property);

    	$log.info('property-ctrl', $scope.properties);
    }
}])
.factory('jarvisWidgetPropertyService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		  property : genericResourceService.crud(['properties'])
	  }
}])
/**
 * properties
 */
.directive('jarvisWidgetProperties', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/jarvis-widget-properties.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisProperties', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/partials/jarvis-properties.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * property
 */
.directive('jarvisWidgetProperty', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/jarvis-widget-property.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisProperty', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/property/partials/jarvis-property-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

