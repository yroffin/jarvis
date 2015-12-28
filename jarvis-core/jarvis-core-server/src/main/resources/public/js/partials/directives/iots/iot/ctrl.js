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

angular.module('JarvisApp.directives.iot', ['JarvisApp.services'])
.controller('iotDirectiveCtrl',
	function($scope, $log, $stateParams, $mdBottomSheet, iotResourceService, toastService){
	
    $scope.remove = function(iot) {
    	$log.debug('delete', iot);
    	iotResourceService.base.delete(iot.id, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' removed');
        	$scope.go('iots');
        }, toastService.failure);
    }

    $scope.save = function(iot) {
    	$log.debug('save', iot);
    	if(iot.owner === '') {
    		iot.owner = undefined;
        	$log.debug('save/owner', iot);
    	}
    	iotResourceService.base.put(iot, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' updated');
        	$scope.go('iots');
        }, toastService.failure);
    }

    $scope.duplicate = function(iot) {
    	$log.debug('duplicate', iot);
    	iotResourceService.base.post(iot, function(element) {
        	toastService.info('Iot ' + iot.name + '#' + iot.id + ' duplicated');
        	$scope.go('iots');
        }, toastService.failure);
    }

    $scope.showBottomSheet = function($event) {
        $mdBottomSheet.show({
          templateUrl: '/ui/js/partials/directives/iot/action.html',
          controller: 'iotDirectiveCtrl',
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
		$scope.allOwners = [{
			id: undefined,
			name: "Empty"
		}];
		$scope.allVisibles = [
		   {id: true,value:'True'},
		   {id: false,value:'False'}
		];
	
		/**
		 * get current iot
		 */
		iotResourceService.base.get($stateParams.id, function(data) {
	    	$scope.iot = data;
	    	toastService.info('Iot ' + data.name + '#' + $stateParams.id);
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
	
		$log.info('iot-directive-ctrl');
    }
})
.directive('iotDirective', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/iots/iot/widget.html',
    link: function(scope, element, attrs) {
    	$log.info('iot-directive');
    }
  }
})
