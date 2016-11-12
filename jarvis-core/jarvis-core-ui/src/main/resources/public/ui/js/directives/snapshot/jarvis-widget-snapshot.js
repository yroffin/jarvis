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

angular.module('jarvis.directives.snapshot', ['JarvisApp.services','JarvisApp.directives.files'])
.controller('snapshotsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetSnapshotService',
	function($scope, $log, genericScopeService, componentService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.snapshots = entities;
			},
			function() {
				return $scope.snapshots;
			},
			$scope, 
			'snapshots', 
			componentService.snapshot,
			{
    			name: "snapshot name",
    			icon: "list"
    		}
	);
}])
.controller('snapshotCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'jarvisWidgetSnapshotService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, componentService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'snapshot', 
			'snapshots', 
			componentService.snapshot);
    /**
     * restore configuration with this snapshot
	 * @param snapshot, the snapshot
     */
    $scope.restore = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		componentService.snapshot.task(snapshot.id, 'restore', {}, function(data) {
       	    	$log.debug('snapshotCtrl::restore', snapshot, data);
    	    }, toastService.failure);
    	}
    }
    /**
     * download current snapshot
	 * @param snapshot, the snapshot to be downloaded
     */
    $scope.download = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		componentService.snapshot.task(snapshot.id, 'download', {}, function(data) {
    			var fileName = 'export-'+$filter('date')(new Date(), 'yyyyMMdd-HHmmss') + '.json';
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
       	    	$log.debug('snapshot', snapshot, data);
       	    	var file = new Blob([angular.toJson(data, true)], {type: 'application/text'});
                var fileURL = window.URL.createObjectURL(file);
                a.href = fileURL;
                a.download = fileName;
                a.click();
    	    }, toastService.failure);
    	}
    }
    /**
     * load local file
	 * @param id of input file
     */
    $scope.upload = function(id) {
    	$('#'+id).trigger('click');
    }
    /**
     * upload callback
	 * @param snapshot, the snapshot
	 * @param file, data to store in snapshot (on client side)
     */
    $scope.loaded = function(snapshot, file) {
    	snapshot.json = file.data;
    	$log.debug('loaded', snapshot, file);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current snapshot
		 */
    	$scope.snapshots = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.snapshot=update}, componentService.snapshot);

		$log.info('snapshot-ctrl', $scope.snapshot);
    }
}])
.factory('jarvisWidgetSnapshotService', [ 'genericResourceService', function(genericResourceService) {
	  return {
		  snapshot : genericResourceService.crud(['snapshots'])
	  }
}])
/**
 * snapshots
 */
.directive('jarvisWidgetSnapshots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/jarvis-widget-snapshots.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisSnapshots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/partials/jarvis-snapshots.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * device
 */
.directive('jarvisWidgetSnapshot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/jarvis-widget-snapshot.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisSnapshot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/snapshot/partials/jarvis-snapshot-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
;

