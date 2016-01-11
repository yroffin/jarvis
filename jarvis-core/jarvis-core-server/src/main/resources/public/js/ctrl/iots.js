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
	function($scope, $log, iotResourceService, genericResourceService, toastService){
    /**
     * Cf. genericResourceService
     */
    $scope.new = function(iots) {
    	genericResourceService.scope.collections.new(
        		'iots',
        		$scope.iots,
        		{
        			name: "...",
        			icon: "star_border"
        		},
        		iotResourceService.iot
        );
    }
    /**
     * loading
     */
	iotResourceService.iot.findAll(function(data) {
        $scope.iots = data;
    }, toastService.failure);

	$log.info('iots-ctrl');
})
.controller('iotCtrl',
	function($scope, $log, $state, $stateParams, iotResourceService, pluginResourceService, genericResourceService, toastService){
    /**
     * Cf. genericResourceService
     */
    $scope.remove = function(iot) {
    	genericResourceService.scope.entity.remove(function() {$scope.go('iots')}, 'iot', iot, iotResourceService.iot);
    }
    /**
     * Cf. genericResourceService
     */
    $scope.save = function(iot) {
    	genericResourceService.scope.entity.save('iot', iot, iotResourceService.iot);
    }
    /**
     * Cf. genericResourceService
     */
    $scope.duplicate = function(iot) {
    	genericResourceService.scope.entity.duplicate(function() {$scope.go('iots')}, 'iot', iot, iotResourceService.iot);
    }
    /**
     * Cf. genericResourceService
     */
    $scope.add = function(plugin) {
    	genericResourceService.scope.link.add($stateParams.id,plugin,{'order':'1'},iotResourceService.plugins,$scope.plugins);
	}
    /**
     * Cf. genericResourceService
     */
    $scope.update = function(plugin) {
    	genericResourceService.scope.link.save($stateParams.id,plugin,iotResourceService.plugins);
	}
    /**
     * Cf. genericResourceService
     */
    $scope.drop = function(plugin) {
    	genericResourceService.scope.link.remove($stateParams.id,plugin,iotResourceService.plugins, $scope.plugins);
	}
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
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true,value:'True'},
		               	   {id: false,value:'False'}
		        ]
		};
	
		/**
		 * get current iot
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.iot=update}, iotResourceService.iot);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, iotResourceService.plugins);
	
		/**
		 * get all combos
		 */
    	$scope.combo.owners = [{id: undefined, name: "iot.empty"}];
    	genericResourceService.scope.combo.findAll('owner', $scope.combo.owners, iotResourceService.iot);
    	$scope.combo.plugins = [{id: undefined, name: "plugin.empty"}];
    	genericResourceService.scope.combo.findAll('plugins', $scope.combo.plugins, pluginResourceService.plugins);
	
		$log.info('iot-ctrl');
	}
})
