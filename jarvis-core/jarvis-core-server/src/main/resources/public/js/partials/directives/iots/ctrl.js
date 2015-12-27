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

angular.module('JarvisApp.directives.iots', ['JarvisApp.services'])
.controller('iotsDirectiveCtrl', 
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
        iotResourceService.base.post(update, function(data) {
                toastService.info('iot ' + data.name + '#' + data.id +' created');
                $scope.iots.push(data);
            }, toastService.failure);
    }

    /**
     * loading jobs
     */
	iotResourceService.base.findAll(function(data) {
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

	$log.info('iots-directive-ctrl');
})
.directive('iotsDirective', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/directives/iots/widget.html',
    link: function(scope, element, attrs) {
    	$log.info('iots-directive');
    }
  }
});
