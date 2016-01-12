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
	function($scope, $log, genericResourceService, viewResourceService, toastService){
    /**
     * create a new view
     * @param views, views list in $scope
     */
    $scope.new = function() {
    	genericResourceService.scope.collections.new(
        		'views',
        		$scope.views,
        		{
        			name: "view name",
        			icon: "star_border"
        		},
        		viewResourceService.view
        );
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.views = [];
		viewResourceService.view.findAll(function(data) {
	        $scope.views = data;
			$log.info('views-ctrl', data);
	    }, toastService.failure);
    }
})
.controller('viewCtrl',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, viewResourceService, iotResourceService, toastService){
	$scope.getLink = function() {
		return $scope.commands;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'view', 
			'views', 
			viewResourceService.view, 
			viewResourceService.iots, 
			{
    			'order':'1'
			},
			$stateParams.id
	);
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
		};
	
		/**
		 * get current view
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.view=update}, viewResourceService.view);
	
		/**
		 * get all views
		 */
		$scope.iots = [];
    	genericResourceService.scope.collections.findAll('iots', $stateParams.id, $scope.iots, viewResourceService.iots);

		/**
		 * find all iots
		 */
    	$scope.combo.iots = [{id: undefined, name: "iot.empty"}];
    	genericResourceService.scope.combo.findAll('owner', $scope.combo.iots, iotResourceService.iot);

		$log.info('view-ctrl', $scope.view);
    }
})
