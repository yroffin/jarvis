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

angular.module('JarvisApp.ctrl.iots', ['JarvisApp.services'])
.controller('iotsCtrl', 
	function($scope, $log, iotResourceService, toastService){
	
    /**
     * create a new job
     * @param job
     */
    $scope.new = function(iots) {
        var update = {
            name: "...",
            icon: "star_border"
        };
        /**
         * create or update this job
         */
        iotResourceService.iot.post(update, function(data) {
                toastService.info('iot ' + data.name + '#' + data.id +' created');
                $scope.iots.push(data);
            }, toastService.failure);
    }

    /**
     * loading iots
     */
	iotResourceService.iot.findAll(function(data) {
        var arr = [];
    	_.forEach(data, function(element) {
            /**
             * convert internal json params
             */
            arr.push({
            	'id':element.id,
            	'name':element.name,
            	'owner':element.name,
            	'visible':element.visible,
            	'icon':element.icon
            });
        });
    	toastService.info(arr.length + ' iot(s)');
        $scope.iots = arr;
    }, toastService.failure);

	$log.info('iots-ctrl');
})
.controller('iotCtrl',
	function($scope, $log, $state, $stateParams, iotResourceService, pluginResourceService, toastService){
	/**
	 * remove
	 * @param iot, the iot to remove
	 */
    $scope.remove = function(iot) {
    	$log.debug('delete', iot);
    	iotResourceService.iot.delete(iot.id, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' removed');
        	$scope.go('iots');
        }, toastService.failure);
    }
	/**
	 * save
	 * @param iot, the iot to save
	 */
    $scope.save = function(iot) {
    	$log.debug('save', iot);
    	if(iot.owner === '') {
    		iot.owner = undefined;
        	$log.debug('save/owner', iot);
    	}
    	iotResourceService.iot.put(iot, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' updated');
        }, toastService.failure);
    }
	/**
	 * duplicate
	 * @param iot, the iot to duplicate
	 */
    $scope.duplicate = function(iot) {
    	$log.debug('duplicate', iot);
    	iotResourceService.iot.post(iot, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' duplicated');
        	$scope.go('iots');
        }, toastService.failure);
    }
      /**
       * add this plugin to this view
       * @param plugin, the plugin to add
       */
      $scope.add = function(plugin) {
      	if(plugin != undefined && plugin.id != undefined && plugin.id != '') {
        	var properties = {
        			'order':'1'
        	};
      		iotResourceService.plugins.post($stateParams.id, plugin.id, properties, function(plugin) {
            	$log.debug('iotCtrl::add', plugin);
      	    	$scope.plugins.push(plugin);
      	    }, toastService.failure);
      	}
      }
      /**
       * update this command
       * @param command, the command to add
       */
      $scope.update = function(plugin) {
      	if(plugin != undefined && plugin.id != undefined && plugin.id != '') {
             	iotResourceService.plugins.put($stateParams.id, plugin.id, plugin.instance, plugin.extended, function(data) {
                 	$log.debug('iotCtrl::update', plugin);
                 	$log.debug('iotCtrl::update', data);
      	    }, toastService.failure);
      	}
      }
      /**
       * drop this plugin from view
       * @param plugin, the plugin to drop
       */
      $scope.drop = function(plugin) {
      	if(plugin != undefined && plugin.id != undefined && plugin.id != '') {
          	$log.debug('drop ', plugin);
          	iotResourceService.plugins.delete($stateParams.id, plugin.id, plugin.instance, function(data) {
      			var toremove = plugin.instance;
      			_.remove($scope.plugins, function(element) {
      				return element.instance == toremove;
      			});
         	    	toastService.info('plugin ' + plugin.name + '#' + plugin.id + ' dropped');
      	    }, toastService.failure);
      	}
      }
      /**
       * render this iot, assume no args by default
       * @param iot, the iot to render
       */
      $scope.render = function(iot) {
      	iotResourceService.iot.task(iot.id, 'render', {}, function(data) {
     	    	$log.debug('iotCtrl::render', data);
     	    	$scope.renderdata = data;
     	    	$scope.output = angular.toJson(data, true);
  	    }, toastService.failure);
      }
      /**
       * init this controller
       */
      $scope.load = function() {
  		$scope.plugins = [];
  		$scope.activeTab = $stateParams.tab;
 
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
					name: "iot.empty"
				}],
		        plugins: [{
					id: undefined,
					name: "plugin.empty"
				}]
		};
	
		/**
		 * get current iot
		 */
		iotResourceService.iot.get($stateParams.id, function(data) {
	    	$scope.iot = data;
	    	toastService.info('Iot ' + data.name + '#' + data.id);
	    }, toastService.failure);
	
		$log.debug('loading owners');
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
		
		$log.debug('loading linked plugins');
		/**
		 * get all plugins
		 */
		iotResourceService.plugins.findAll($stateParams.id, function(plugins) {
	    	$scope.plugins = plugins;
	    	$log.debug('Linked plugins', $scope.plugins);
	    	toastService.info($scope.plugins.length + ' plugins (linked)');
	    }, toastService.failure);

		$log.debug('loading available plugins');
		/**
		 * find all plugins
		 */
		pluginResourceService.plugins.findAll(function(plugins) {
	    	_.forEach(plugins, function(plugin) {
	            /**
	             * convert internal json params
	             */
	    		$scope.combo.plugins.push({
	            	'id':plugin.id,
	            	'name':plugin.name
	            });
	        });
	    }, toastService.failure);
	
		$log.info('iot-ctrl');
    }
})
