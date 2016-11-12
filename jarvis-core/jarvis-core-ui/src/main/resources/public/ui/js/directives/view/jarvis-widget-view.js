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

angular.module('jarvis.directives.view', ['JarvisApp.services'])
.controller('viewsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetViewService',
	function($scope, $log, genericScopeService, componentService){
    $log.info('viewsCtrl');
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.views = entities;
			},
			function() {
				return $scope.views;
			},
			$scope, 
			'views', 
			componentService.view,
			{
    			name: "view name",
    			icon: "list"
    		}
	);

	$log.info('views-ctrl', $scope.views);
}])
.controller('viewCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'jarvisWidgetViewService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'view', 
			'views', 
			componentService.view);
	/**
	 * declare links
	 */
	$scope.links = {
			devices: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.devices;
			},
			$scope.links.devices, 
			'view', 
			'views', 
			componentService.view, 
			componentService.devices, 
			{
    			'order':'1'
			},
			$stateParams.id
	);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current view
		 */
    	$scope.views = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.view=update}, componentService.view);
	
		/**
		 * get all views
		 */
		$scope.devices = [];
    	genericResourceService.scope.collections.findAll('devices', $stateParams.id, $scope.devices, componentService.devices);

		$log.info('view-ctrl', $scope.views);
    }
}])
.factory('jarvisWidgetViewService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  view : genericResourceService.crud(['views']),
	  devices : genericResourceService.links(['views'], ['devices'])
  }
}])
/**
 * Views
 */
.directive('jarvisWidgetViews', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/jarvis-widget-views.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisViews', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/partials/jarvis-views.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * View
 */
.directive('jarvisWidgetView', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/jarvis-widget-view.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisViewGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/view/partials/jarvis-view-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

