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
        pluginResourceService.base.post(update, function(data) {
                toastService.info('plugin ' + data.name + '#' + data.id +' created');
                $scope.plugins.push(data);
            }, toastService.failure);
    }

    $scope.load = function() {
	    /**
	     * loading plugins
	     */
		pluginResourceService.base.findAll(function(data) {
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
    	    	$scope.commands.push(data);
    	    	toastService.debug('script ' + data.name + '#' + $stateParams.id + ' updated');
    	    }, toastService.failure);
    	}
    }
    
    $scope.load = function() {
    	$scope.command = {};
    	
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
	    	toastService.debug(commands.length + ' commands');
	    }, toastService.failure);

		/**
		 * find all owner
		 */
		iotResourceService.base.findAll(function(data) {
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
