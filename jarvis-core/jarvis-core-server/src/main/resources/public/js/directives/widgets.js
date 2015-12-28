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

angular.module('JarvisApp.directives.widgets', ['JarvisApp.services'])
.directive('jarvisPlugins', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-plugins.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-plugins');
    }
  }
})
.directive('jarvisPluginCommon', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/jarvis-plugin-common.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-common');
	    }
	  }
})
.directive('jarvisPluginScript', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/jarvis-plugin-script.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-script');
	    }
	  }
})
.directive('jarvisIots', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-iots.html',
    link: function(scope, element, attrs) {
    	$log.info('iots-directive');
    }
  }
})
.directive('jarvisIot', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-iot.html',
    link: function(scope, element, attrs) {
    	$log.info('iot-directive');
    }
  }
})
.directive('jarvisJobs', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-jobs.html',
    link: function(scope, element, attrs) {
    	$log.info('jobs-directive');
    }
  }
})
.directive('jarvisJob', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-job.html',
    link: function(scope, element, attrs) {
    	$log.info('job-directive');
    }
  }
})
.directive('jarvisBottomSheetIot', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-bottom-sheet-iot.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-iot');
    }
  }
})
.directive('jarvisBottomSheetPlugin', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-bottom-sheet-plugin.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-plugin');
    }
  }
})
