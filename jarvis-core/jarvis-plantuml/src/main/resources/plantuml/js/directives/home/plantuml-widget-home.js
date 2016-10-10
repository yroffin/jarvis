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

/* Directives */

angular.module('plantuml.directives.home', ['plantuml.app.services'])
.controller('homeCtrl',
		[ '$scope', '$log', '$stateParams', 'plantumlWidgetHomeService', 'toastService',
	function($scope, $log, $stateParams, plantumlHomeService, toastService){
    /**
     * loading
     */
    $scope.load = function() {
		$log.info('homeCtrl loaded');
    }
}])
.factory('plantumlWidgetHomeService', [ function() {
	return {
	}
}])
/**
 * plantumlWidgetHome
 */
.directive('plantumlWidgetHome', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/js/directives/home/plantuml-widget-home.html',
    link: function(scope, element, attrs) {
		$log.info('plantuml-widget-home loaded');
    }
  }
}])
;
