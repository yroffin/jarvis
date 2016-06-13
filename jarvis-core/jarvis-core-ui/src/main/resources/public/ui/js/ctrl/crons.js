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

angular.module('JarvisApp.ctrl.crons', ['JarvisApp.services'])
.controller('cronsCtrl', 
		['$scope', '$log', 'genericScopeService', 'cronResourceService', 'toastService',
	function($scope, $log, genericScopeService, cronResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.crons = entities;
			},
			function() {
				return $scope.crons;
			},
			$scope, 
			'crons', 
			cronResourceService.cron,
			{
    			name: "cron name",
    			icon: "list",
    			cron: "* * * * *"
    		}
	);
    /**
     * start all crontab
     */
    $scope.start = function() {
    	/**
    	 * iterate on each cron
    	 */
    	_.each($scope.crons, function(cron) {
	    	cronResourceService.cron.task(cron.id, 'toggle', {target:true}, function(data) {
	   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
		    }, toastService.failure);
    	});
    }
    /**
     * stop all crontab
     */
    $scope.stop = function() {
    	/**
    	 * iterate on each cron
    	 */
    	_.each($scope.crons, function(cron) {
	    	cronResourceService.cron.task(cron.id, 'toggle', {target:false}, function(data) {
	   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
		    }, toastService.failure);
    	});
    }
}])
.controller('cronCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'cronResourceService', 'iotResourceService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, cronResourceService, iotResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'cron', 
			'crons', 
			cronResourceService.cron);
    /**
     * toggle cron status
     */
    $scope.toggle = function(cron) {
    	$log.info(cron);
    	cronResourceService.cron.task(cron.id, 'toggle', {}, function(data) {
   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
	    }, toastService.failure);
    }
    /**
     * test cron status
     */
    $scope.test = function(cron) {
    	$log.info(cron);
    	cronResourceService.cron.task(cron.id, 'test', {}, function(data) {
   	    	toastService.info('crontab ' + crontab.name + '#' + crontab.id + ' toggled to ' + crontab.status);
	    }, toastService.failure);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current cron
		 */
    	$scope.crons = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.cron=update}, cronResourceService.cron);

		$log.info('cron-ctrl', $scope.crons);
    }
}])
