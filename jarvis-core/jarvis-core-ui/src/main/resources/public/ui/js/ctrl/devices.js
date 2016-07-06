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

angular.module('JarvisApp.ctrl.devices', ['JarvisApp.services'])
.controller('devicesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'deviceResourceService',
	function($scope, $log, genericScopeService, deviceResourceService){
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
			deviceResourceService.device,
			{
    			name: "object name",
    			icon: "list"
    		}
	);
}])
.controller('deviceCtrl',
		['$scope', '$log', '$state', '$stateParams', 'deviceResourceService', 'pluginResourceService', 'genericScopeService', 'genericResourceService', 'toastService',
	function($scope, $log, $state, $stateParams, deviceResourceService, pluginResourceService, genericScopeService, genericResourceService, toastService){
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
			deviceResourceService.device
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
			deviceResourceService.device, 
			deviceResourceService.plugins, 
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
			deviceResourceService.device, 
			deviceResourceService.triggers, 
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
			deviceResourceService.device, 
			deviceResourceService.devices, 
			{'order':'1'},
			$stateParams.id
	);
    /**
	 * render this device, assume no args by default
	 * @param device, the device to render
	 */
	$scope.render = function(device) {
	 	deviceResourceService.device.task(device.id, 'render', {}, function(data) {
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
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.device=update}, deviceResourceService.device);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, deviceResourceService.plugins);
    	$scope.devices = [];
    	genericResourceService.scope.collections.findAll('devices', $stateParams.id, $scope.devices, deviceResourceService.devices);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, deviceResourceService.triggers);
	
		$log.info('device-ctrl');
	}
}]);
