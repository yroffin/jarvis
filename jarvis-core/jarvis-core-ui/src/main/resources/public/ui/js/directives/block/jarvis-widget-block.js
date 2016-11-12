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

angular.module('jarvis.directives.block', ['JarvisApp.services'])
.controller('blocksCtrl', 
		[ '$scope',
		  '$log',
		  'genericScopeService',
		  'genericPickerService',
		  'jarvisWidgetBlockService',
		  'toastService', 
	function(
			$scope,
			$log,
			genericScopeService,
			genericPickerService,
			componentService,
			toastService){
	$log.debug('blocks-ctrl', $scope.blocks);
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
			componentService.block,
			{
    			name: "block name",
    			icon: "list",
    			parameter: "{}"
    		}
	);

	$log.debug('blocks-ctrl', $scope.blocks);
}])
.controller('blockCtrl',
	[ '$scope',
	  '$log',
	  '$stateParams',
	  'genericScopeService',
	  'genericResourceService',
	  'genericPickerService',
	  'jarvisWidgetBlockService',
	  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			componentService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'block', 
			'blocks', 
			componentService.block
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins:{
				"if": {},
				"then": {},
				"else": {}
			},
			blocks:{
				"then": {},
				"else": {}
			}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.if;
			},
			$scope.links.plugins.if,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.if, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.then;
			},
			$scope.links.plugins.then,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.else;
			},
			$scope.links.plugins.else,
			'block',
			'blocks',
			componentService.block, 
			componentService.plugins.else, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.then;
			},
			$scope.links.blocks.then,
			'block',
			'blocks',
			componentService.block, 
			componentService.blocks.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.else;
			},
			$scope.links.blocks.else,
			'block',
			'blocks',
			componentService.block, 
			componentService.blocks.else, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * task
     */
    $scope.test = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		componentService.block.task(block.id, 'test', {}, function(data) {
       	    	$scope.testExpression = data == "true";
    	    }, toastService.failure);
    	}
    }
    /**
     * task
     */
    $scope.execute = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		componentService.block.task(block.id, 'execute', {}, function(data) {
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
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.block=update}, componentService.block);

		/**
		 * get all plugins
		 */
    	$scope.plugins = {
    			if:{},
    			then:{},
    			else:{}
    	};
    	$scope.plugins.if = [];
    	genericResourceService.scope.collections.findAll('plugins.if', $stateParams.id, $scope.plugins.if, componentService.plugins.if);
    	$scope.plugins.then = [];
    	genericResourceService.scope.collections.findAll('plugins.then', $stateParams.id, $scope.plugins.then, componentService.plugins.then);
    	$scope.plugins.else = [];
    	genericResourceService.scope.collections.findAll('plugins.else', $stateParams.id, $scope.plugins.else, componentService.plugins.else);

		/**
		 * get all plugins
		 */
    	$scope.blocks = {
    			then:{},
    			else:{}
    	};
    	$scope.blocks.then = [];
    	genericResourceService.scope.collections.findAll('blocks.then', $stateParams.id, $scope.blocks.then, componentService.blocks.then);
    	$scope.blocks.else = [];
    	genericResourceService.scope.collections.findAll('blocks.else', $stateParams.id, $scope.blocks.else, componentService.blocks.else);

    	$log.debug('block-ctrl', $scope.block);
    	$log.debug('block-ctrl', $scope.blocks);
    }
}])
.factory('jarvisWidgetBlockService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		    block: genericResourceService.crud(['blocks']),
		    plugins: {
		    	"if": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_IF'),
			    "then": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_THEN'),
			    "else": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_ELSE')
		    },
			blocks: {
				"then": genericResourceService.links(['blocks'], ['blocks'],'HREF_THEN'),
				"else": genericResourceService.links(['blocks'], ['blocks'],'HREF_ELSE')
			}
	  }
}])
/**
 * blocks
 */
.directive('jarvisWidgetBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/jarvis-widget-blocks.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * block
 */
.directive('jarvisWidgetBlock', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/jarvis-widget-block.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockElse', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-else.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisBlockThen', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/block/partials/jarvis-block-then.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;