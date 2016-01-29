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

angular.module('JarvisApp.ctrl.blocks', ['JarvisApp.services'])
.controller('blocksCtrl', 
	function($scope, $log, genericScopeService, genericPickerService, blockResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.blocks = entities;
			},
			function() {
				return $scope.blocks;
			},
			$scope, 
			'blocks', 
			blockResourceService.block,
			{
    			name: "block name",
    			icon: "list"
    		}
	);
})
.controller('blockCtrl',
	function(
			$scope,
			$log,
			$stateParams,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			blockResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'block', 
			'blocks', 
			blockResourceService.block
	);
    /**
     * pick an element
     * @param node
     */
    $scope.pickIot = function(node) {
    	$scope.block.pluginId = node.id;
    	$scope.block.pluginName = node.name;
    }
    /**
     * clear plugin
     */
    $scope.clearPlugin = function() {
    	$scope.block.pluginId = undefined;
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickThenIot = function(node) {
    	$scope.block.pluginThenId = node.id;
    	$scope.block.pluginThenName = node.name;
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickElseIot = function(node) {
    	$scope.block.pluginElseId = node.id;
    	$scope.block.pluginElseName = node.name;
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickThenBlock = function(node) {
    	$scope.block.blockThenId = node.id;
    	$scope.block.blockThenName = node.name;
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickElseBlock = function(node) {
    	$scope.block.blockElseId = node.id;
    	$scope.block.blockElseName = node.name;
    }
    /**
     * clear
     */
    $scope.clearPluginThen = function() {
    	$log.debug('Clear then plugin');
    	$scope.block.pluginThenId = undefined;
    	$scope.block.pluginThenName = undefined;
    }
    /**
     * clear
     */
    $scope.clearBlockThen = function() {
    	$log.debug('Clear then block');
    	$scope.block.blockThenId = undefined;
    	$scope.block.blockThenName = undefined;
    }
    /**
     * clear
     */
    $scope.clearPluginElse = function() {
    	$log.debug('Clear else plugin');
    	$scope.block.pluginElseId = undefined;
    	$scope.block.pluginElseName = undefined;
    }
    /**
     * clear
     */
    $scope.clearBlockElse = function() {
    	$log.debug('Clear else block');
    	$scope.block.blockElseId = undefined;
    	$scope.block.blockElseName = undefined;
    }
    /**
     * task
     */
    $scope.test = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		blockResourceService.block.task(block.id, 'test', {}, function(data) {
       	    	$log.debug('[BLOCK/test]', block, data);
       	    	$scope.testExpression = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * task
     */
    $scope.execute = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		blockResourceService.block.task(block.id, 'execute', {}, function(data) {
       	    	$log.debug('[BLOCK/test]', block, data);
       	    	$scope.testExpression = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.block=update}, blockResourceService.block);

		$log.debug('block-ctrl', $scope.scenario);
    }
})
