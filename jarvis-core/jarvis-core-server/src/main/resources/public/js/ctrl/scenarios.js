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

angular.module('JarvisApp.ctrl.scenarios', ['JarvisApp.services'])
.controller('scenariosCtrl', 
	function($scope, $log, genericResourceService, scenarioResourceService, toastService){
    /**
     * create a new scenario
     * @param scenarios, scenarios list in $scope
     */
    $scope.new = function(scenarios) {
    	genericResourceService.scope.collections.new(
        		'scenarios',
        		$scope.scenarios,
        		{
        			name: "scenario name",
        			icon: "list"
        		},
        		scenarioResourceService.scenario
        );
    }
    /**
     * loading
     */
    $scope.load = function() {
    	$scope.scenarios = [];
		scenarioResourceService.scenario.findAll(function(data) {
	    	$scope.scenarios = data;
			$log.debug('scenarios-ctrl', data);
	    }, toastService.failure);
    }
})
.controller('scenarioCtrl',
	function($scope, $log, $stateParams, genericScopeService, genericResourceService, scenarioResourceService, blockResourceService, pluginResourceService, toastService){
	$scope.getLink = function() {
		return $scope.commands;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario, 
			scenarioResourceService.blocks, 
			{
				'order':'1'
			},
			$stateParams.id
	);
    /**
     * add then block
     * @param block, block to modify
     */
    $scope.addThenBlock = function(block) {
    	$log.debug('iotCtrl::addThenBlock', block);
    }
    /**
     * add then block
     * @param block, block to modify
     */
    $scope.addElseBlock = function(block) {
    	$log.debug('iotCtrl::addElseBlock', block);
    }
    /**
     * add then block
     * @param block, block to modify
     */
    $scope.addThenAction = function(block, plugin) {
    	$log.debug('iotCtrl::addThenAction', block, plugin);
    }
    /**
     * add then block
     * @param block, block to modify
     */
    $scope.addElseAction = function(block, plugin) {
    	$log.debug('iotCtrl::addElseAction', block, plugin);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
  		$scope.plugin = {};

	    /**
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true,value:'True'},
		               	   {id: false,value:'False'}
		        ],
		        plugins: []
		};
	
		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.scenario=update}, scenarioResourceService.scenario);

		/**
		 * find all plugins
		 */
    	$scope.combo.plugins = [];
    	genericResourceService.scope.combo.findAll('plugins', $scope.combo.plugins, pluginResourceService.plugins);

		/**
		 * find all iots
		 */
    	$scope.blocks = [];
    	genericResourceService.scope.collections.findAll('blocks', $stateParams.id, $scope.blocks, scenarioResourceService.blocks);

		$log.debug('scenario-ctrl', $scope.scenario);
    }
})
