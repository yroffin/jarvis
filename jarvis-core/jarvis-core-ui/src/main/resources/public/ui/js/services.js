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

/* Services */

var jarvisServicesUrl = '/services';
var jarvisApiUrl = '/api';
var myAppServices = angular.module('JarvisApp.services', [ 'ngResource' ]);

/**
 * toastService
 */
myAppServices.factory('toastService', [ '$log', '$mdToast', function($log, $mdToast) {
  var $log =  angular.injector(['ng']).get('$log');
  $log.info('toastService', $mdToast);

  var toastServiceInstance;
  toastServiceInstance = {
		toastPosition: {
		          bottom: true,
		          top: false,
		          left: false,
		          right: true
		},
		getToastPosition: function() {
		     return Object.keys(toastServiceInstance.toastPosition)
		         .filter(function(pos) { return toastServiceInstance.toastPosition[pos]; })
		         .join(' ');
		},
        failure: function(failure) {
        	$log.error(failure);
            $mdToast.show(
                    $mdToast.simple()
                        .content(failure)
                        .position(toastServiceInstance.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
        },
        info: function(message) {
        	$log.info(message,toastServiceInstance.getToastPosition());
            $mdToast.show(
                    $mdToast.simple()
                        .position(toastServiceInstance.getToastPosition())
                        .textContent(message)
                        .hideDelay(3000)
                        .capsule(true)
                );
        }
  }
  return toastServiceInstance;
}]);

/**
 * $store service
 */
myAppServices.factory('$store', [ '$log', '$rootScope', function($log, $rootScope) {
  $log.info("$store");

  /**
   * collection the main store var
   * all pushed data ae stored in this object
   */
  var collection = {};
  
  var methods = {
	        collection: collection,
	        /**
	         * pus data in store, data must be a plain object
	         */
	        push: function(classname, instance, data) {
	        	/**
	        	 * create classname map if does not exist
	        	 */
	        	if(collection[classname] === undefined) {
	        		collection[classname] = {};
	        	}
	        	/**
	        	 * store new data
	        	 */
	        	$log.info("$store", classname, instance, data);
	            collection[classname][instance] = data;
	        },
	        /**
	         * retrieve value
	         */
	    	get: function(classname, instance, def) {
	    		if(collection == undefined) return def;
	    		if(collection[classname] == undefined) return def;
	    		if(collection[classname][instance] == undefined) return def;
	    		return collection[classname][instance];
	    	}	        
	      };

  return methods;
}]);

/**
 * crontabResourceService
 */
myAppServices.factory('oauth2ResourceService', 
		[
		 '$rootScope',
		 '$log',
		 '$window',
		 '$mdDialog',
		 '$location',
		 'Restangular',
		 function(
				 $rootScope,
				 $log,
				 $window,
				 $mdDialog,
				 $location,
				 Restangular
				 ) {
		  var $log =  angular.injector(['ng']).get('$log');
		  return {
			  	/**
			  	 * me service retrieve current user identity
			  	 */
		        me: function(token, callback, failure) {
		        	$log.info("Fix JarvisAuthToken to", token);
		        	Restangular.setDefaultHeaders ({
		        		'JarvisAuthToken' : token
		        	}); 
		        	Restangular.one('/api/profile/me').get().then(
		        		function(profile) {
			        		callback(profile);
			        	},function(errors){
			        		failure(errors);
			        	}
			        );
		        },
			  	/**
			  	 * retrieve oauth2 identity
			  	 */
		        config: function(args, callback, failure) {
		        	Restangular.one('oauth2').get(args).then(
		        		function(profile) {
			        		callback(profile);
			        	},function(errors){
			        		failure(errors);
			        	}
			        );
		        },
			  	/**
			  	 * connect
			  	 */
		        connect: function($scope, token) {
			        $log.info('connect with', token);
		        	var self = this;
		        	self.me(
		        		token,
	        			function(data) {
	        				$log.info('profile', data);
	        				$mdDialog.hide();
	        				$rootScope.profile = data;
		    	        	$scope.boot();
		    	    		$log.info('Switch to home');
		    	        	$location.path("/home");
	        			},
	        			function(failure) {
	        				$log.warn('no profile');
	        				self.login();
	        			}
		        	);
		        },
			  	/**
			  	 * login
			  	 */
		        login: function() {
		        	$mdDialog.show({
		        	      controller: 'oauth2DialogCtrl',
		        	      templateUrl: '/ui/js/partials/dialog/oauth2Dialog.tmpl.html',
		        	      parent: angular.element(document.body),
		        	      clickOutsideToClose:false,
		        	      fullscreen: false
		        	});
		        }
		  }
		}
]);
