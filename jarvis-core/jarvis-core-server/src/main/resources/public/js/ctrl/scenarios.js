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
	function($scope, $log, scenarioResourceService, toastService){
    /**
     * create a new scenario
     * @param scenarios, scenarios list in $scope
     */
    $scope.new = function(scenarios) {
        var update = {
            name: "Todo",
            icon: "list"
        };
        /**
         * create or update this scenario
         */
        $log.debug('scenariosCtrl::new', update);
        scenarioResourceService.scenario.post(update, function(data) {
                $log.debug('scenario ' + data.name + '#' + data.id +' created');
                $scope.scenarios.push(data);
            }, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.scenarios = [];
    	
	    /**
	     * loading scenarios
	     */
		scenarioResourceService.scenario.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	            arr.push(element);
	        });
	    	$log.debug(arr.length + ' scenario(s)');
	        $scope.scenarios = arr;
	    }, toastService.failure);
	
		$log.debug('scenarios-ctrl');
    }
})
.controller('scenarioCtrl',
	function($scope, $log, $stateParams, $mdBottomSheet, scenarioResourceService, iotResourceService, toastService){
	/**
	 * remove this scenario
	 * @param scenario, the scenario to remove
	 */
    $scope.remove = function(scenario) {
    	$log.debug('delete', scenario);
    	scenarioResourceService.scenario.delete(scenario.id, function(element) {
        	toastService.info('scenario ' + scenario.name + '#' + scenario.id + ' removed');
        	$scope.go('scenarios');
        }, toastService.failure);
    }
	/**
	 * save this scenario
	 * @param scenario, the scenario to save
	 */
    $scope.save = function(scenario) {
    	$log.debug('save', scenario);
    	if(scenario.owner === '') {
    		scenario.owner = undefined;
        	$log.debug('save/owner', scenario);
    	}
    	scenarioResourceService.scenario.put(scenario, function(element) {
        	toastService.info('scenario ' + scenario.name + '#' + scenario.id + ' updated');
        }, toastService.failure);
    }
    /**
     * duplicate this scenario
     */
    $scope.duplicate = function(scenario) {
    	$log.debug('scenarioCtrl::duplicate', scenario);
    	scenarioResourceService.scenario.post(scenario, function(element) {
        	toastService.info('scenario ' + scenario.name + '#' + scenario.id + ' duplicated');
        	$scope.go('scenarios');
        }, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
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
		 * get current scenario
		 */
		scenarioResourceService.scenario.get($stateParams.id, function(data) {
	    	$scope.scenario = data;
	    	$log.debug('scenario ' + data.name + '#' + $stateParams.id);
	    }, toastService.failure);
	
		$log.debug('scenario-ctrl');
    }
})
