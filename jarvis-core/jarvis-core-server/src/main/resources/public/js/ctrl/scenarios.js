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
	$scope.setEntities = function(entities) {
		$scope.scenarios = entities;
	}
	$scope.getEntities = function() {
		return $scope.scenarios;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
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
	$scope.getLink = function() {
		return $scope.blocks;
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
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickBlockDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
    		$scope.add(node);
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickBlockDialogCtrl');
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickIotDialog = function(ev, block) {
    	return genericPickerService.pickers.iots(ev, function(node) {
        	block.context = node;
        	block.plugin = node.id;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickIotDialogCtrl');
    }
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
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true, value:'True'},
		               	   {id: false,value:'False'}
		        ]
		};
	
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
    	genericResourceService.scope.collections.findAll('blocks', $stateParams.id, $scope.blocks, scenarioResourceService.blocks);

		/**
		 * find all plugins
		 */
    	$scope.combo.plugins = [];
    	genericResourceService.scope.combo.findAll('plugins', $scope.combo.plugins, pluginResourceService.plugins);

		$log.debug('scenario-ctrl', $scope.scenario);
    }
})
.controller('blocksCtrl', 
	function($scope, $log, genericScopeService, genericPickerService, blockResourceService, toastService){
	$scope.setEntities = function(entities) {
		$scope.blocks = entities;
	}
	$scope.getEntities = function() {
		return $scope.blocks;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
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
	$scope.getLink = function() {
		return $scope.blocks;
	}
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
     * @param ev
     * @param block
     */
    $scope.pickIotDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
        	block.pluginId = node.id;
        	block.pluginName = node.name;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickIotDialogCtrl');
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickThenIotDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
        	block.pluginThenId = node.id;
        	block.pluginThenName = node.name;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickIotDialogCtrl');
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickElseIotDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
        	block.pluginElseId = node.id;
        	block.pluginElseName = node.name;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickIotDialogCtrl');
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickThenBlockDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
        	block.blockThenId = node.id;
        	block.blockThenName = node.name;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickBlockDialogCtrl');
    }
    /**
     * pick an element
     * @param ev
     * @param block
     */
    $scope.pickElseBlockDialog = function(ev, block) {
    	return genericPickerService.pickers.nodes(ev, function(node) {
        	block.blockElseId = node.id;
        	block.blockElseName = node.name;
    	}, function() {
    		$log.debug('Abort');
    	},
    	'pickBlockDialogCtrl');
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
