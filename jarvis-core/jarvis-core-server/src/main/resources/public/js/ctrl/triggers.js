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

angular.module('JarvisApp.ctrl.triggers', ['JarvisApp.services'])
.controller('triggersCtrl', 
	function($scope, $log, genericScopeService, genericResourceService, triggerResourceService, toastService){
	$scope.setEntities = function(entities) {
		$scope.triggers = entities;
	}
	$scope.getEntities = function() {
		return $scope.triggers;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			$scope, 
			'triggers',
			triggerResourceService.trigger
	);
	/**
	 * pre create
	 */
	$scope.preCreate = function(entity) {
		$scope.create(entity, function(data){
    		var removed = _.remove($scope.triggers, function(trigger) {
    			return	trigger.device == data.device &&
    					trigger.plugin == data.plugin &&
    					trigger.field == data.field &&
    					trigger.id == undefined
    		});
    		data.status = 'current';
			$log.debug('postCreate', data, removed);
		});
	}
	$scope.preRemove = function(entity) {
		$scope.remove(entity, function(data){
			$log.debug('preRemove', data);
			var real = _.clone(data);
			real.status = 'added';
			$scope.triggers.push(real);
		});
	}
    /**
     * check actual triggers
     * @param triggers
     */
    $scope.preLoad = function() {
    	$scope.load(function() {
	    	$log.debug('triggersCtrl::check', $scope.triggers);
	    	_.each($scope.triggers, function(real) {
	    		real.status = 'current';
	    	});
	    	triggerResourceService.trigger.task('*', 'test', {}, function(data) {
	        	$log.debug('triggersCtrl::check', data);
	        	$scope.crossref =  data;
	        	_.find($scope.crossref, function(real) {
	        		var index = _.findIndex($scope.triggers, function(trigger) {
	        			return	trigger.device == real.device &&
	        					trigger.plugin == real.plugin &&
	        					trigger.field == real.field
	        		});
	        		if(index < 0) {
	        			real.status = 'added';
	        			$scope.triggers.push(real);
	        		}
	        	});
		    }, toastService.failure);
    	});
    }
    /**
     * rebuild all triggers
     * @param triggers
     */
    $scope.rebuild = function() {
    	$log.debug('triggersCtrl::check', $scope.triggers);
    }
})
;