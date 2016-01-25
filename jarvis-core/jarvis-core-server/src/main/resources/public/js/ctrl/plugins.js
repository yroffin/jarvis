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

angular.module('JarvisApp.ctrl.plugins', ['JarvisApp.services'])
.controller('pluginsCtrl', 
	function($scope, $log, genericScopeService, pluginResourceService){
	$scope.setEntities = function(entities) {
		$scope.plugins = entities;
	}
	$scope.getEntities = function() {
		return $scope.plugins;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			$scope, 
			'plugins', 
			pluginResourceService.scripts,
			{
    			name: "script name",
    			icon: "list",
       			type: "script"
    		}
	);
})
.controller('pluginScriptCtrl',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, genericPickerService, pluginResourceService, iotResourceService, commandResourceService, toastService){
	$scope.getLink = function() {
		return $scope.commands;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'script', 
			'plugins', 
			pluginResourceService.scripts, 
			pluginResourceService.commands, 
			{
    			'order':'1',
    			'name':'noname',
       			'nature':'info',
       			'type':'json'
			},
			$stateParams.id
	);
    /**
     * execute this command on server side
	 * @param command, the command to be executed
     */
    $scope.execute = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
    		pluginResourceService.scripts.task(command.id, 'execute', $scope.rawoutput, function(data) {
       	    	$log.debug('pluginScriptCtrl::execute', command, data);
       	    	$scope.rawoutput = data;
       	    	$scope.output = angular.toJson(data, true);
    	    }, toastService.failure);
    	}
    }
    /**
     * clear params
     */
    $scope.clear = function() {
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    }
    /**
     * load controller
     */
    $scope.load = function() {
    	$scope.activeTab = $stateParams.tab;
    	
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    	
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
		 * get current script
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.script=update}, pluginResourceService.scripts);
	
		/**
		 * get all commands
		 */
    	$scope.commands = [];
    	genericResourceService.scope.collections.findAll('commands', $stateParams.id, $scope.commands, pluginResourceService.commands);

		/**
		 * find all owner
		 */
    	$scope.combo.owners = [{id: undefined, name: "iot.empty"}];
    	genericResourceService.scope.combo.findAll('owner', $scope.combo.owners, iotResourceService.iot);

		$log.info('script-ctrl');
    }
})
