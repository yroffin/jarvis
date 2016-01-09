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
	function($scope, $log, commandResourceService, toastService){
	
    /**
     * create a new job
     * @param job
     */
    $scope.new = function(commands) {
        var update = {
            name: "...",
            icon: "star_border"
        };
        /**
         * create or update this job
         */
        commandResourceService.base.post(update, function(data) {
                toastService.info('command ' + data.name + '#' + data.id +' created');
                $scope.commands.push(data);
            }, toastService.failure);
    }

    $scope.load = function() {
	    /**
	     * loading commands
	     */
		commandResourceService.base.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	            arr.push(element);
	        });
	    	toastService.info(arr.length + ' command(s)');
	        $scope.commands = arr;
	    }, toastService.failure);
	
		$log.info('commands-ctrl');
    }
})
.controller('commandCtrl',
	function($scope, $log, $stateParams, commandResourceService, iotResourceService, toastService){
	
    $scope.remove = function(command) {
    	$log.debug('delete', command);
    	commandResourceService.base.delete(command.id, function(element) {
        	toastService.info('command ' + command.name + '#' + command.id + ' removed');
        	$scope.go('commands');
        }, toastService.failure);
    }

    $scope.save = function(command, callback, params) {
    	$log.debug('commandCtrl::save', command);
    	commandResourceService.base.put(command, function(element) {
        	toastService.info('command ' + command.name + '#' + command.id + ' updated');
        	if(callback) {
        		if(params) {
        			callback(params);
        		} else {
        			callback(element);
        		}
        	}
        }, toastService.failure);
    }
    /**
     * execute this command
     * @param command, the command to execute
     */
    $scope.execute = function(command) {
    	$log.debug('commandCtrl::execute', command);
    	commandResourceService.ext.task(command.id, 'execute', $scope.input, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' executed');
   	    	$log.debug(data);
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }

    $scope.pretty = function() {
    	$log.debug('pretty', $scope.rawinput);
    	$scope.jsoninput = angular.toJson($scope.rawinput, true);
    	$scope.input = angular.fromJson($scope.jsoninput, true);
    }

    $scope.duplicate = function(command) {
    	$log.debug('duplicate', command);
    	commandResourceService.base.post(command, function(element) {
        	toastService.info('command ' + command.name + '#' + command.id + ' duplicated');
        	$scope.go('commands');
        }, toastService.failure);
    }
      
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
		commandResourceService.base.get($stateParams.id, function(data) {
	    	$scope.command = data;
	    	toastService.info('command ' + data.name + '#' + $stateParams.id);
	    }, toastService.failure);
	
		$log.info('command-ctrl');
    }
})
