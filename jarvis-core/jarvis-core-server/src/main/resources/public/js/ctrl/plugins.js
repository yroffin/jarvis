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
	function($scope, $log, pluginResourceService, toastService){
	
    /**
     * create a new job
     * @param job
     */
    $scope.new = function(plugins) {
        var update = {
            name: "...",
            icon: "star_border"
        };
        /**
         * create or update this job
         */
        pluginResourceService.scripts.post(update, function(data) {
                toastService.info('plugin ' + data.name + '#' + data.id +' created');
                $scope.plugins.push(data);
            }, toastService.failure);
    }

    $scope.load = function() {
	    /**
	     * loading plugins
	     */
		pluginResourceService.plugins.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	            arr.push(element);
	        });
	    	toastService.info(arr.length + ' plugin(s)');
	        $scope.plugins = arr;
	    }, toastService.failure);
	
		$log.info('plugins-ctrl');
    }
})
.controller('pluginScriptCtrl',
	function($scope, $log, $stateParams, $mdBottomSheet, pluginResourceService, iotResourceService, commandResourceService, toastService){
	
    $scope.remove = function(script) {
    	$log.debug('delete', script);
    	pluginResourceService.scripts.delete(script.id, function(element) {
        	toastService.info('script ' + script.name + '#' + script.id + ' removed');
        	$scope.go('plugins');
        }, toastService.failure);
    }

    $scope.save = function(script) {
    	$log.debug('save', script);
    	if(script.owner === '') {
    		script.owner = undefined;
        	$log.debug('save/owner', script);
    	}
    	pluginResourceService.scripts.put(script, function(element) {
        	toastService.info('script ' + script.name + '#' + script.id + ' updated');
        	$scope.go('plugins');
        }, toastService.failure);
    }

    $scope.duplicate = function(script) {
    	$log.debug('duplicate', script);
    	pluginResourceService.scripts.post(script, function(element) {
        	toastService.info('script ' + script.name + '#' + script.id + ' duplicated');
        	$scope.go('plugins');
        }, toastService.failure);
    }

    $scope.showBottomSheet = function($event) {
        $mdBottomSheet.show({
          template: '<jarvis-bottom-sheet-plugin></jarvis-bottom-sheet-plugin>',
          controller: 'pluginScriptCtrl',
          preserveScope: true,
          scope: $scope,
          targetEvent: $event,
          clickOutsideToClose: true
        }).then(function(clickedItem) {
        	$mdBottomSheet.hide();
        },function(clickedItem) {
        	$mdBottomSheet.hide();
        });
      };
      
    $scope.add = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
        	$log.debug(command);
    		pluginResourceService.commands.put($stateParams.id, command.id, function(data) {
    			commandResourceService.base.get(data.id, function(command) {
        	    	$scope.commands.push(command);
        	    	toastService.info('script ' + command.name + '#' + command.id + ' added');
    			});
    	    }, toastService.failure);
    	}
    }
    
    $scope.drop = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
        	$log.debug('drop ', command);
    		pluginResourceService.commands.delete($stateParams.id, command.id, command.instance, function(data) {
    			var toremove = command.instance;
    			_.remove($scope.commands, function(element) {
    				return element.instance == toremove;
    			});
       	    	toastService.info('command ' + command.name + '#' + command.id + ' dropped');
    	    }, toastService.failure);
    	}
    }
    /**
     * execute this command on server side
	 * @param command, the command to be executed
     */
    $scope.execute = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
        	commandResourceService.ext.task(command.id, 'execute', $scope.rawoutput, function(data) {
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
    	$scope.commands = [];
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    	
	    /**
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true,value:'True'},
		               	   {id: false,value:'False'}
		        ],
		        owners: [{
					id: undefined,
					name: "owner.empty"
				}],
		        commands: [{
					id: undefined,
					name: "command.empty"
				}]
		};
	
		/**
		 * get current script
		 */
		pluginResourceService.scripts.get($stateParams.id, function(data) {
	    	$scope.script = data;
	    	toastService.info('script ' + data.name + '#' + $stateParams.id);
	    }, toastService.failure);
	
		/**
		 * get all commands
		 */
		pluginResourceService.commands.findAll($stateParams.id, function(data) {
	    	$scope.commands = data;
	    	$log.debug('Linked commands', $scope.commands);
	    	toastService.info($scope.commands.length + ' commands (linked)');
	    }, toastService.failure);

		/**
		 * find all owner
		 */
		iotResourceService.iot.findAll(function(data) {
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	    		$scope.combo.owners.push({
	            	'id':element.id,
	            	'name':element.name
	            });
	        });
	    }, toastService.failure);
	
		/**
		 * find all owner
		 */
		commandResourceService.base.findAll(function(data) {
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	    		$scope.combo.commands.push({
	            	'id':element.id,
	            	'name':element.name
	            });
	        });
	    }, toastService.failure);

		$log.info('script-ctrl');
    }
})
