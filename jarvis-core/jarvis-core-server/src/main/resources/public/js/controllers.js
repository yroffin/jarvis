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

angular.module('JarvisApp.config',[])
     .config(function($mdIconProvider) {
	  // Configure URLs for icons specified by [set:]id.
	})
	.config(['$translateProvider', 
	    function($translateProvider){
		  // Register a loader for the static files
		  // So, the module will search missing translation tables under the specified urls.
		  // Those urls are [prefix][langKey][suffix].
		  $translateProvider.useStaticFilesLoader({
		    prefix: 'js/partials/l10n/',
		    suffix: '.json'
		  });
		  // Tell the module what language to use by default
		  $translateProvider.preferredLanguage('fr_FR');
		  $translateProvider.useSanitizeValueStrategy(null);
	}])
    .config(['RestangularProvider',
        function(RestangularProvider) {
    		RestangularProvider.setBaseUrl('/api');
    		RestangularProvider.setDefaultHeaders({ 'content-type': 'application/json' });
    		/**
    		 * request interceptor
    		 */
    	    RestangularProvider.setFullRequestInterceptor(function(element, operation, route, url, headers, params, httpConfig) {
    	      return {
    	        element: element,
    	        params: params,
    	        headers: headers,
    	        httpConfig: httpConfig
    	      };
    	    });
    	    RestangularProvider.setResponseExtractor(function(response) {
    	    	  var newResponse = response;
    	    	  newResponse.originalElement = response;
    	    	  return newResponse
    	    });
    }])
    .config(function($mdThemingProvider){
        // Configure a dark theme with primary foreground yellow
        $mdThemingProvider.theme('default')
            .primaryPalette('blue', {
                'default': '600',
                'hue-1': '50',
                'hue-2': '600',
                'hue-3': 'A700'
              })
        	.accentPalette('indigo')
        	.warnPalette('red')
        	.backgroundPalette('grey', {
                'default': '50',
                'hue-1': '50',
                'hue-2': '600',
                'hue-3': 'A700'
              });
    })
    /**
     * main controller
     */
    .controller('JarvisAppCtrl',
    	function($scope, $mdSidenav, $location, $state){
        /**
         * initialize jarvis configuration
         */
        $scope.inventoryItem = {
            href : ''
        };
        $scope.jarvis = {};
        $scope.jarvis.newjob = {};
        $scope.jarvis.configuration = {};
        $scope.jarvis.neo4jdb = {};

        /**
         * highlight JS
         */
        hljs.initHighlightingOnLoad();
        
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
    })
