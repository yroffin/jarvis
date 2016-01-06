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

angular.module('JarvisApp.ctrl.views', ['JarvisApp.services'])
.controller('viewsCtrl', 
	function($scope, $log, viewResourceService, toastService){
    /**
     * create a new view
     * @param views, views list in $scope
     */
    $scope.new = function(views) {
        var update = {
            name: "Todo",
            icon: "list"
        };
        /**
         * create or update this view
         */
        $log.debug('viewsCtrl::new', view);
        viewResourceService.base.post(update, function(data) {
                toastService.info('view ' + data.name + '#' + data.id +' created');
                $scope.views.push(data);
            }, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.views = [];
    	
	    /**
	     * loading views
	     */
		viewResourceService.base.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	            arr.push(element);
	        });
	    	toastService.info(arr.length + ' view(s)');
	        $scope.views = arr;
	    }, toastService.failure);
	
		$log.info('views-ctrl');
    }
})
.controller('viewCtrl',
	function($scope, $log, $stateParams, $mdBottomSheet, viewResourceService, iotResourceService, toastService){
	/**
	 * remove this view
	 * @param view, the view to remove
	 */
    $scope.remove = function(view) {
    	$log.debug('delete', view);
    	viewResourceService.base.delete(view.id, function(element) {
        	toastService.info('view ' + view.name + '#' + view.id + ' removed');
        	$scope.go('views');
        }, toastService.failure);
    }
	/**
	 * save this view
	 * @param view, the view to save
	 */
    $scope.save = function(view) {
    	$log.debug('save', view);
    	if(view.owner === '') {
    		view.owner = undefined;
        	$log.debug('save/owner', view);
    	}
    	viewResourceService.base.put(view, function(element) {
        	toastService.info('view ' + view.name + '#' + view.id + ' updated');
        }, toastService.failure);
    }
    /**
     * duplicate this view
     */
    $scope.duplicate = function(view) {
    	$log.debug('viewCtrl::duplicate', view);
    	viewResourceService.base.post(view, function(element) {
        	toastService.info('view ' + view.name + '#' + view.id + ' duplicated');
        	$scope.go('views');
        }, toastService.failure);
    }
    /**
     * bottom sheet handler
     */
    $scope.showBottomSheet = function($event) {
        $mdBottomSheet.show({
          template: '<jarvis-bottom-sheet-view></jarvis-bottom-sheet-view>',
          controller: 'viewCtrl',
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
    /**
     * add this plugin to this view
     * @param plugin, the plugin to add
     */
    $scope.add = function(iot) {
    	if(iot != undefined && iot.id != undefined && iot.id != '') {
        	$log.debug('add', iot);
    		viewResourceService.iots.put($stateParams.id, iot.id, function(data) {
    			iotResourceService.iot.get(data.id, function(iot) {
        	    	$scope.iots.push(iot);
        	    	toastService.info('iot ' + iot.name + '#' + iot.id + ' added');
    			});
    	    }, toastService.failure);
    	}
    }
    /**
     * drop this plugin from view
     * @param plugin, the plugin to drop
     */
    $scope.drop = function(iot) {
    	if(iot != undefined && iot.id != undefined && iot.id != '') {
        	$log.debug('drop ', iot);
    		viewResourceService.iots.delete($stateParams.id, iot.id, iot.instance, function(data) {
    			var toremove = iot.instance;
    			_.remove($scope.iots, function(element) {
    				return element.instance == toremove;
    			});
       	    	toastService.info('iot ' + iot.name + '#' + iot.id + ' dropped');
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.iots = {};
    	
	    /**
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true,value:'True'},
		               	   {id: false,value:'False'}
		        ],
		        iots: [{
					id: undefined,
					name: "iot.empty"
				}]
		};
	
		/**
		 * get current view
		 */
		viewResourceService.base.get($stateParams.id, function(data) {
	    	$scope.view = data;
	    	toastService.info('view ' + data.name + '#' + $stateParams.id);
	    }, toastService.failure);
	
		/**
		 * get all iots
		 */
		viewResourceService.iots.findAll($stateParams.id, function(data) {
	    	$scope.iots = data;
	    	$log.debug('Linked iots', $scope.iots);
	    	toastService.info($scope.iots.length + ' iots (linked)');
	    }, toastService.failure);

		/**
		 * find all iots
		 */
		iotResourceService.iot.findAll(function(data) {
	    	_.forEach(data, function(element) {
	            /**
	             * convert internal json params
	             */
	    		$scope.combo.iots.push({
	            	'id':element.id,
	            	'name':element.name
	            });
	        });
	    }, toastService.failure);

		$log.info('view-ctrl');
    }
})
