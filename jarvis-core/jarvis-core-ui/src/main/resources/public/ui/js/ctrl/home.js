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

angular.module('JarvisApp.ctrl.home', ['JarvisApp.services'])
.controller('homeCtrl', 
		['$scope', '$rootScope', '$store', '$log', 'viewResourceService', 'iotResourceService', 'toastService', 'oauth2ResourceService',
	function($scope, $rootScope, $store, $log, viewResourceService, iotResourceService, toastService, oauth2ResourceService){
    /**
     * swipe left
     */
    $scope.onSwipeLeft = function() {
    	if($scope.tabIndex >= ($scope.views.length-1)) {
    		$scope.tabIndex = $scope.views.length-1;
    		return;
    	}
    	$scope.tabIndex++;
    }
    /**
     * swipe right
     */
    $scope.onSwipeRight = function() {
    	if($scope.tabIndex <= 0) {
    		$scope.tabIndex = 0;
    		return;
    	}
    	$scope.tabIndex--;
    }
    /**
     * select tab callback
     */
    $scope.selectTab = function(view, index) {
    	$scope.tabIndex = index;
    }
    /**
     * is selected
     */
    $scope.activeTab = function(view, index) {
    	return $scope.tabIndex == index;
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.checkProfile();

    	$scope.store = $store;
    	$scope.views = [];
    	$scope.tabIndex = -1;
    	
	    /**
	     * loading views
	     */
		viewResourceService.view.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * load this view only if ishome
	             */
	            if(element.ishome) {
	            	element.urlBackground = "url('"+element.url+"')";
	            	arr.push(element);
	            }
	        });
	    	toastService.info(arr.length + ' view(s)');
	    	
	    	_.forEach(arr, function(view) {
	    		$log.info('loading view', view);
	    		viewResourceService.iots.findAll(view.id, function(data) {
	    			view.iots = data;
	    			var done = _.after(view.iots.length, function() {
	    				$log.debug('Linked iots to view', view.iots);
	    			});
	    			_.forEach(view.iots, function(iot){
	    				/**
	    				 * render each view
	    				 */
	    		      	iotResourceService.iot.task(iot.id, 'render', {}, function(data) {
	    		      		iot.render = data;
	    		      		done(iot);
	    		      	}, toastService.failure);
	    			});
	    	    }, toastService.failure);
			});
	    	
	        $scope.views = arr;
	        if($scope.views.length > 0) {
	        	$scope.tabIndex = 0;
	        }
	    }, toastService.failure);
	
		$log.info('home-ctrl');
    }
}])
.controller('helperCtrl', 
		[ '$scope', '$store', '$log', 'viewResourceService', 'iotResourceService', 'toastService',
	function($scope, $store, $log, viewResourceService, iotResourceService, toastService){
}])
