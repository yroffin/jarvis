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

/* Controllers */

angular.module('plantuml.app.config',[])
    .config(['RestangularProvider', function(RestangularProvider) {
		RestangularProvider.setDefaultHeaders({
			'content-type': 'application/json'
		});

		/**
		 * request interceptor
		 */
	    RestangularProvider.setFullRequestInterceptor(function(element, operation, route, url, headers, params, httpConfig) {
	    	headers['content-type'] = 'application/json';
			return {
			  element: element,
			  params: params,
			  headers: headers,
			  httpConfig: httpConfig
			};
	    });
		/**
		 * answer interceptor
		 */
	    RestangularProvider.setResponseExtractor(function(response) {
	    	if(angular.isObject(response)) {
	    	  var newResponse = response;
	    	  newResponse.originalElement = response;
	    	  return newResponse
	    	} else {
	    		return response;
	    	}
	    });
    }])
    .config(['$mdIconProvider', function($mdIconProvider) {
	}])
	.config(['$translateProvider', function($translateProvider){
		// Register a loader for the static files
		// So, the module will search missing translation tables under the specified urls.
		// Those urls are [prefix][langKey][suffix].
		$translateProvider.useStaticFilesLoader({
			prefix: 'js/l10n/',
			suffix: '.json'
		});
		// Tell the module what language to use by default
		$translateProvider.preferredLanguage('fr_FR');
		$translateProvider.useSanitizeValueStrategy(null);
	}])
    /**
     * main controller
     */
    .controller('PlantumlAppCtrl',
    		['$rootScope',
    		 '$scope',
    		 '$log',
    		 '$http',
    		 '$mdDialog',
    		 '$mdSidenav',
    		 '$mdMedia',
    		 '$location',
    		 '$window',
    		 '$state',
    	function(
    			$rootScope,
    			$scope,
    			$log,
    			$http,
    			$mdDialog,
    			$mdSidenav,
    			$mdMedia,
    			$location,
    			$window,
    			$state){
        $scope.isJson = 
			function isJson(str) {
			    try {
			        JSON.parse(str);
			    } catch (e) {
			        return false;
			    }
			    return true;
			};
    	/**
         * default value
         */
        $scope.defaultValue = function(value, def) {
        	if(value === undefined) {
        		return def;
        	}
        	return value
        }
        
        /**
         * toggle navbar
         * @param menuId
         */
        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
        };

        /**
         * load configuration
         */
        $scope.location = function(menuId, target) {
            $mdSidenav(menuId).toggle();
            $location.path(target);
        };

        /**
         * go to state
         */
        $scope.go = function(target,params) {
            $state.go(target,params);
        };

        /**
         * bootstrap this controller
         */
    	$scope.boot = function() {
            $log.info('PlantumlAppCtrl configured');
    	}
    }])
;