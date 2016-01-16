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

angular.module('JarvisApp.ctrl.commands', ['JarvisApp.services'])
.controller('commandsCtrl', 
	function($scope, $log, genericScopeService, commandResourceService){
	$scope.setEntities = function(entities) {
		$scope.commands = entities;
	}
	$scope.getEntities = function() {
		return $scope.commands;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			$scope, 
			'commands', 
			commandResourceService.command,
			{
    			name: "command name",
    			icon: "list"
    		}
	);
})
.controller('commandCtrl',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, commandResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'command', 
			'commands', 
			commandResourceService.command
	);
    /**
     * execute this command
     * @param command, the command to execute
     */
    $scope.execute = function(command) {
    	$log.debug('commandCtrl::execute', command);
    	commandResourceService.command.task(command.id, 'execute', $scope.input, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' executed');
   	    	$log.debug(data);
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * transform command
     */
    $scope.pretty = function() {
    	$log.debug('pretty', $scope.rawinput);
    	$scope.jsoninput = angular.toJson($scope.rawinput, true);
    	$scope.input = angular.fromJson($scope.jsoninput, true);
    }
    /**
     * loading
     */
    $scope.load = function() {
	    /**
	     * init part
	     */
		$scope.combo = {
				visibles: [
		               	   {
		               		   id: true,
		               		   value:'common.true'
		               	   },
		               	   {
		               		   id: false,
		               		   value:'common.false'
		               	   }
		       ],
		       types: [
		              	   {
		              		   id: 'SHELL',
		              		   value:'command.shell'
		              	   },
		              	   {
		              		   id: 'COMMAND',
		              		   value:'command.single'
		              	   },
		              	   {
		              		   id: 'GROOVY',
		              		   value:'command.groovy'
		              	   },
		              	   {
		              		   id: 'SSH',
		              		   value:'command.ssh'
		              	   }
		      ]
		}
		/**
		 * input test
		 */
		$scope.rawinput='{"default":"todo"}';
		$scope.jsoninput = angular.toJson($scope.rawinput, true);
		$scope.input = angular.fromJson($scope.jsoninput);
		/**
		 * get current command
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.command=update}, commandResourceService.command);
	
		$log.info('command-ctrl');
    }
})
