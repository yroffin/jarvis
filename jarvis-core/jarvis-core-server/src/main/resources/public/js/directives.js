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


angular.module('JarvisApp.directives', []).
    directive('appVersion', ['version', function(version) {
      return function(scope, elm, attrs) {
        elm.text(version);
      };
    }]);

angular.module('JarvisApp.directives', [])
/**
 * job-directive
 */
.controller('jobDirectiveCtrl',
['$scope', '$log', function($scope, $log){
	$log.info('job-directive-ctrl');
}])
.directive('jobDirective', ['$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/job-directive.html',
    link: function(scope, element, attrs) {
    	$log.info('job-directive', scope.job);
    }
  }
}])
/**
 * job-detail-directive
 */
.controller('jobDetailDirectiveCtrl',
['$scope', '$log', function($scope, $log){
	$log.info('job-directive-detail-ctrl');
}])
.directive('jobDetailDirective', ['$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/job-detail-directive.html',
    link: function(scope, element, attrs) {
    	$log.info('job-detail-directive', scope.job);
    }
  }
}])
/**
 * iot-directive
 */
.controller('iotDirectiveCtrl',
['$scope', '$log', function($scope, $log){
	$log.info('iot-directive-ctrl');
	
	$scope.test = function() {
		$log.info('test',$scope.iot);
	}
}])
.directive('iotDirective', ['$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/iot-directive.html',
    link: function(scope, element, attrs) {
    	$log.info('iot-directive', scope.iot);
    }
  }
}])
