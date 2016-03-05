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

angular.module('JarvisApp.ctrl.connectors', ['JarvisApp.services'])
.controller('connectorsCtrl', 
	function($scope, $log, genericScopeService, connectorResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.connectors = entities;
			},
			function() {
				return $scope.connectors;
			},
			$scope, 
			'connectors', 
			connectorResourceService.connector,
			{
    			name: "connector name",
    			icon: "settings_input_antenna"
    		}
	);
})
.controller('connectorCtrl',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, connectorResourceService, connexionResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'connector', 
			'connectors', 
			connectorResourceService.connector);
	/**
	 * declare links
	 */
	$scope.links = {
			connexions: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.connexions;
			},
			$scope.links.connexions, 
			'connector', 
			'connectors', 
			connectorResourceService.connector, 
			connectorResourceService.connexions, 
			{
			},
			$stateParams.id,
			{
				/**
				 * drop callback, to delete target connexion (orignal remove is cancelled)
				 * @param data
				 * @returns
				 */
				remove: function(data) {
					connexionResourceService.connexion.delete(data.id, function() {
						$log.info("Drop any ", data);
			  			var toremove = data.instance;
			  			_.remove($scope.connexions, function(element) {
			  				return element.instance == toremove;
			  			});
					}, function() {
						$log.warn("While dropping any ", data);
					});
				}
			}
	);
    /**
     * register a new connexion
     */
    $scope.register = function(ev, connector) {
    	/**
    	 * display a dialog box to register this new connexion
    	 */
        $mdDialog.show({
        	  controller: function(scope) {
        		scope.connexion = {
        				href: 'http://...',
        				isRenderer:false,
        				isSensor:true,
        				canAnswer:false
        		}
    	    	scope.answer = function(abort, cnx) {
    	    	    if(abort === true) {
    	    	    	$mdDialog.hide(cnx);
    	    	    } else {
    	    	    	$mdDialog.cancel();
    	    	    }
    	    	};
        	  },
	          templateUrl: 'js/partials/dialog/connexionsDialog.tmpl.html',
	          parent: angular.element(document.body),
	          targetEvent: ev,
	          clickOutsideToClose:true
	        })
	        .then(function(connexion) {
	        	$log.debug("ok", connexion);
	        	connectorResourceService.connector.task(connector.id, 'register', connexion, function(data) {
		        	$log.debug("callback", connexion);
	        	});
	        }, function() {
	        });
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current connector
		 */
    	$scope.connectors = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.connector=update}, connectorResourceService.connector);

		/**
		 * get all views
		 */
		$scope.connexions = [];
    	genericResourceService.scope.collections.findAll('connexions', $stateParams.id, $scope.connexions, connectorResourceService.connexions);

    	$log.info('connector-ctrl', $scope.connectors);
    }
})
