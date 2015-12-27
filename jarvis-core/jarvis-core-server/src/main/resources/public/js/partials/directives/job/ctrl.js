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

angular.module('JarvisApp.directives.job', ['JarvisApp.services'])
.controller('jobDirectiveCtrl',
	function($scope, $log, $stateParams, jobResourceService, paramResourceService, toastService){
	
    $scope.remove = function(chip) {
    	jobResourceService.params.delete($scope.job.id, chip.id, chip.instance, function(element) {
        	toastService.info('Parameter ' + chip.value + '#' + chip.id + ' :: ' + element.instance + ' removed');
        }, toastService.failure);
    }

    $scope.append = function(chip) {
    	jobResourceService.params.put($scope.job.id, chip.id, function(element) {
        	toastService.info('Parameter ' + chip.value + '#' + chip.id + ' :: ' + element.instance + ' added');
        	chip.instance = element.instance;
        }, toastService.failure);
    }

    $scope.querySearch = function(query) {
        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);
            return function filterFn(param) {
            	console.error(param);
              return (param.value.indexOf(lowercaseQuery) === 0) ||
                  (param.type.indexOf(lowercaseQuery) === 0);
            };
          }

        var results = query ? $scope.allParams.filter(createFilterFor(query)) : [];
        return results;
      }

	$log.debug('job-directive-ctrl loading');

	$scope.params = [];
	$scope.allParams = [];

	jobResourceService.get($stateParams.id, function(data) {
    	$scope.job = data;
    	toastService.info('Job ' + data.name + '#' + $stateParams.id);
        jobResourceService.params.findAll($stateParams.id, function(params) {
        	$scope.params = params;
        	toastService.info(params.length + ' parameter(s)');
        	/**
        	 * load all params
        	 */
            paramResourceService.findAll(function(params) {
            	$scope.allParams = params;
            	toastService.info(params.length + ' parameter(s) loaded');
            }, toastService.failure);
        }, toastService.failure);
    }, toastService.failure);

	$log.debug('job-directive-ctrl loaded');
})
.directive('jobDirective', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/job/widget.html',
    link: function(scope, element, attrs) {
    	$log.info('job-directive');
    }
  }
})
