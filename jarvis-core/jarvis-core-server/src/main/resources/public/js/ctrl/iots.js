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

angular.module('JarvisApp.ctrl.iots', ['JarvisApp.services'])
.controller('iotsCtrl', 
	function($scope, $log, genericScopeService, iotResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.iots = entities;
			},
			function() {
				return $scope.iots;
			},
			$scope, 
			'iots', 
			iotResourceService.iot,
			{
    			name: "object name",
    			icon: "list"
    		}
	);
})
.controller('iotCtrl',
	function($scope, $log, $state, $stateParams, iotResourceService, pluginResourceService, genericScopeService, genericResourceService, toastService){
	$scope.getLink = function() {
		return $scope.plugins;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'iot', 
			'iots', 
			iotResourceService.iot
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins: {},
			triggers: {},
			iots: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins;
			},
			$scope.links.plugins,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.plugins, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.triggers, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.iots;
			},
			$scope.links.iots,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.iots, 
			{'order':'1'},
			$stateParams.id
	);
    /**
	 * render this iot, assume no args by default
	 * @param iot, the iot to render
	 */
	$scope.render = function(iot) {
	 	iotResourceService.iot.task(iot.id, 'render', {}, function(data) {
	 		$log.debug('iotCtrl::render', data);
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
		 * get current iot
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.iot=update}, iotResourceService.iot);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, iotResourceService.plugins);
    	$scope.iots = [];
    	genericResourceService.scope.collections.findAll('iots', $stateParams.id, $scope.iots, iotResourceService.iots);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, iotResourceService.triggers);
	
		$log.info('iot-ctrl');
	}
})
