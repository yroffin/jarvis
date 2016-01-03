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
	function($scope, $log, viewResourceService, toastService){
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
	             * load this view only if ishome
	             */
	            if(element.ishome) {
	            	arr.push(element);
	            }
	        });
	    	toastService.info(arr.length + ' view(s)');
	        $scope.views = arr;
	    }, toastService.failure);
	
		$log.info('views-ctrl');
    }
})
