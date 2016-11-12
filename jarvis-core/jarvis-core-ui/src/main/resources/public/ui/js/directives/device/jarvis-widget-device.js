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

angular.module('jarvis.directives.device', ['JarvisApp.services'])
.controller('devicesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetDeviceService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.devices = entities;
			},
			function() {
				return $scope.devices;
			},
			$scope, 
			'devices', 
			componentService.device,
			{
    			name: "object name",
    			icon: "list"
    		}
	);
}])
.controller('deviceCtrl',
		['$scope', '$log', '$state', '$stateParams', 'jarvisWidgetDeviceService', 'genericScopeService', 'genericResourceService', 'toastService',
	function($scope, $log, $state, $stateParams, componentService, genericScopeService, genericResourceService, toastService){
	$scope.getLink = function() {
		return $scope.plugins;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'device', 
			'devices', 
			componentService.device
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins: {},
			triggers: {},
			devices: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins;
			},
			$scope.links.plugins,
			'device',
			'devices',
			componentService.device, 
			componentService.plugins, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'device',
			'devices',
			componentService.device, 
			componentService.triggers, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.devices;
			},
			$scope.links.devices,
			'device',
			'devices',
			componentService.device, 
			componentService.devices, 
			{'order':'1'},
			$stateParams.id
	);
    /**
	 * render this device, assume no args by default
	 * @param device, the device to render
	 */
	$scope.render = function(device) {
		componentService.device.task(device.id, 'render', {}, function(data) {
	 		$log.debug('deviceCtrl::render', data);
	 	    $scope.renderdata = data;
	 	    $scope.output = angular.toJson(data, true);
	    }, toastService.failure);
	}
	/**
	 * init this controller
	 */
	$scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
	 
		/**
		 * get current device
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.device=update}, componentService.device);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, componentService.plugins);
    	$scope.devices = [];
    	genericResourceService.scope.collections.findAll('devices', $stateParams.id, $scope.devices, componentService.devices);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, componentService.triggers);
	
		$log.info('device-ctrl');
	}
}])
.factory('jarvisWidgetDeviceService', [ 'genericResourceService', function(genericResourceService) {
	return {
		device: genericResourceService.crud(['devices']),
		plugins : genericResourceService.links(['devices'], ['plugins','scripts']),
		devices : genericResourceService.links(['devices'], ['devices']),
		triggers : genericResourceService.links(['devices'], ['triggers'])
	}
}])
/**
 * devices
 */
.directive('jarvisWidgetDevices', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/jarvis-widget-devices.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDevices', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-devices.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * device
 */
.directive('jarvisWidgetDevice', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/jarvis-widget-device.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDeviceGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDevicePlugin', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-plugin.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisDeviceRender', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/device/partials/jarvis-device-render.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;
