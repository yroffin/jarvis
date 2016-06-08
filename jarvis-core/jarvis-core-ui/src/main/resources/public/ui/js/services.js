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
 * clientResourceService
 */
myAppServices.factory('clientResourceService', [ '$q', '$window', '$rootScope', 'Restangular', function($q, $window, $rootScope, Restangular) {
  var $log =  angular.injector(['ng']).get('$log');
  $log.info('clientResourceService', $q);
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('clients').getList().then(function(clients) {
        		callback(clients);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
}]);

/**
 * crontabResourceService
 */
myAppServices.factory('crontabResourceService', [ '$q', '$window', '$rootScope', 'Restangular', function($q, $window, $rootScope, Restangular) {
  var $log =  angular.injector(['ng']).get('$log');
  $log.info('crontabResourceService', $q);
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('crontabs').getList().then(function(crontabs) {
        	  callback(crontabs);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
}]);

/**
 * crontabResourceService
 */
myAppServices.factory('oauth2ResourceService', 
		[
		 '$rootScope',
		 '$window',
		 'Restangular',
		 function(
				 $rootScope,
				 $window,
				 Restangular
				 ) {
		  var $log =  angular.injector(['ng']).get('$log');
		  return {
			  	/**
			  	 * me service retrieve current user identity
			  	 */
		        me: function(callback, failure) {
		        	Restangular.setDefaultHeaders ({
		        		'JarvisAuthToken' : $rootScope.accessToken
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
		        }
		  }
		}
]);
