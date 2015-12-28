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

/* Directives */

angular.module('JarvisApp.directives.plugins.script', ['JarvisApp.services'])
.controller('pluginScriptDirectiveCtrl',
	function($scope, $log, $stateParams, $mdBottomSheet, pluginResourceService, iotResourceService, toastService){
	
    $scope.remove = function(script) {
    	$log.debug('delete', script);
    	pluginResourceService.scripts.delete(script.id, function(element) {
        	toastService.info('script ' + script.name + '#' + script.id + ' removed');
        	$scope.go('plugins');
        }, toastService.failure);
    }

    $scope.save = function(script) {
    	$log.debug('save', script);
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
          templateUrl: '/ui/js/partials/directives/plugins/script/action.html',
          controller: 'pluginScriptDirectiveCtrl',
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
      
    $scope.load = function() {
	    /**
	     * init part
	     */
		$scope.allOwners = [];
		$scope.allBooleans = [
		               	   {id: true,value:'True'},
		               	   {id: false,value:'False'}
		               	];
	
		/**
		 * get current script
		 */
		pluginResourceService.scripts.get($stateParams.id, function(data) {
	    	$scope.script = data;
	    	toastService.info('script ' + data.name + '#' + $stateParams.id);
	    }, toastService.failure);
	
		/**
		 * find all owner
		 */
		iotResourceService.base.findAll(function(data) {
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	    		$scope.allOwners.push({
	            	'id':element.id,
	            	'name':element.name
	            });
	        });
	    }, toastService.failure);
	
		$log.info('script-directive-ctrl');
    }
})
.directive('pluginScriptDirective', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/plugins/script/widget.html',
    link: function(scope, element, attrs) {
    	$log.info('plugin-script-directive');
    }
  }
})
