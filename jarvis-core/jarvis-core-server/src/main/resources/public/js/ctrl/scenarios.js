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
	function($scope, $log, genericScopeService, scenarioResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.scenarios = entities;
			},
			function() {
				return $scope.scenarios;
			},
			$scope, 
			'scenarios', 
			scenarioResourceService.scenario,
			{
    			name: "scenario name",
    			icon: "list"
    		}
	);
})
.controller('scenarioCtrl',
	function(
			$scope,
			$log,
			$stateParams,
			$mdDialog,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			scenarioResourceService,
			blockResourceService,
			pluginResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario);
	/**
	 * declare links
	 */
	$scope.links = {
			blocks: {},
			triggers: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks;
			},
			$scope.links.blocks,
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
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario,
			scenarioResourceService.triggers, 
			{
				'order':'1'
			},
			$stateParams.id
	);
    /**
     * render scenario
     */
    $scope.build = function(resource) {
    	var codes = [];
    	_.each(resource, function(graph) {
        	var code = "";
	    	_.each(graph.nodes, function(node) {
	    		if(node.start) {
	    			code += '#' + node.id + '=>start: ' + node.name + ':>'+node.description+'\n';
	    		}
	    		if(node.end) {
	    			code += '#' + node.id + '=>end: ' + node.name + ':>'+node.description+'\n';
	    		}
	    		if(node.activity) {
	    			code += '#' + node.id + '=>operation: ' + node.name + ':>'+node.description+'\n';
	    		}
	    		if(node.gateway) {
	    			code += '#' + node.id + '=>condition: ' + node.name + ':>'+node.description+'\n';
	    		}
	    		if(node.call) {
	    			code += '#' + node.id + '=>subroutine: ' + node.name + ':>'+node.description+'\n';
	    		}
	    	});
	    	_.each(graph.edges, function(edge) {
	    		if(graph.nodes[edge.sourceId].gateway) {
	        		if(edge.bool) {
	        			code += '#' + edge.sourceId + '(yes, right)->#' + edge.targetId + '\n';
	        		} else {
	        			code += '#' + edge.sourceId + '(no)->#' + edge.targetId + '\n';
	        		}
	    		} else {
	    			code += '#' + edge.sourceId + '->#' + edge.targetId + '\n';
	    		}
	    	});
        	codes.push(code);
    	});
    	return codes;
    }
    $scope.render = function(resources) {
		$log.debug(resources);
    	$('#canvas').html('');
    	_.each(resources, function(resource) {
	    	var chart = flowchart.parse(resource);
	        chart.drawSVG('canvas', {
	          // 'x': 30,
	          // 'y': 50,
	          'line-width': 3,
	          'line-length': 50,
	          'text-margin': 10,
	          'font-size': 14,
	          'font': 'normal',
	          'font-family': 'Helvetica',
	          'font-weight': 'normal',
	          'font-color': 'black',
	          'line-color': 'black',
	          'element-color': 'black',
	          'fill': 'white',
	          'yes-text': 'yes',
	          'no-text': 'no',
	          'arrow-end': 'block',
	          'scale': 1,
	          'symbols': {
	            'start': {
	              'font-color': 'red',
	              'element-color': 'green',
	              'fill': 'yellow'
	            },
	            'end':{
	              'class': 'end-element'
	            }
	          },
	          'flowstate' : {
	            'past' : { 'fill' : '#CCCCCC', 'font-size' : 12},
	            'current' : {'fill' : 'yellow', 'font-color' : 'red', 'font-weight' : 'bold'},
	            'future' : { 'fill' : '#FFFF99'},
	            'request' : { 'fill' : 'blue'},
	            'invalid': {'fill' : '#444444'},
	            'approved' : { 'fill' : '#58C4A3', 'font-size' : 12, 'yes-text' : 'APPROVED', 'no-text' : 'n/a' },
	            'rejected' : { 'fill' : '#C45879', 'font-size' : 12, 'yes-text' : 'n/a', 'no-text' : 'REJECTED' }
	          }
	        });
    	});
    }
    /**
     * load this controller
     */
    $scope.chart = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		scenarioResourceService.scenario.task(scenario.id, 'render', {}, function(data) {
       	    	$log.debug('[SCENARIO/render]', scenario, data);
       	    	$scope.code = $scope.build(data);
       	    	$scope.render($scope.code);
    	    }, toastService.failure);
    	}
    }
    /**
     * execute this scenario
	 * @param scenario, the scenario to be executed
     */
    $scope.execute = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		scenarioResourceService.scenario.task(scenario.id, 'execute', {}, function(data) {
       	    	$log.debug('[SCENARIO/execute]', scenario, data);
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
  		$scope.plugin = {};

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.scenario=update;
    		$scope.chart(update);
    	}, scenarioResourceService.scenario);

		/**
		 * find all blocks
		 */
    	$scope.blocks = [];
    	genericResourceService.scope.collections.findAll(
    			'blocks', $stateParams.id, $scope.blocks, scenarioResourceService.blocks,
    			function(blocks) {
    				_.forEach(blocks, function(block) {
    					block.plugins = {};
    					block.plugins.if   = [];
    					block.plugins.then = [];
    					block.plugins.else = [];
    					blockResourceService.plugins.if.findAll(block.id, function(data){
    						block.plugins.if = data;
    					});
    					blockResourceService.plugins.then.findAll(block.id, function(data){
    						block.plugins.then = data;
    					});
    					blockResourceService.plugins.else.findAll(block.id, function(data){
    						block.plugins.else = data;
    					});
    					block.blocks = {};
    					block.blocks.then = [];
    					block.blocks.else = [];
    					blockResourceService.blocks.then.findAll(block.id, function(data){
    						block.blocks.then = data;
    					});
    					blockResourceService.blocks.else.findAll(block.id, function(data){
    						block.blocks.else = data;
    					});
    					$log.debug("blocks.plugins", block.plugins);
    					$log.debug("blocks.blocks", block.blocks);
    				});
    			}
    	);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, scenarioResourceService.triggers);

		$log.debug('scenario-ctrl', $scope.scenario);
    }
})
