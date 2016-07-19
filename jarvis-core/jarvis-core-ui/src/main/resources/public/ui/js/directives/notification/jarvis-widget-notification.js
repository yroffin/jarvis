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

angular.module('jarvis.directives.notification', ['JarvisApp.services'])
.controller('notificationsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetNotificationService',
		function($scope, $log, genericScopeService, notificationResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.notifications = entities;
			},
			function() {
				return $scope.notifications;
			},
			$scope, 
			'notifications', 
			notificationResourceService.notification,
			{
    			name: "notification name",
    			icon: "settings_remote"
    		}
	);
}])
.controller('notificationCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'genericPickerService', 'jarvisWidgetNotificationService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, genericPickerService, notificationResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'notification', 
			'notifications', 
			notificationResourceService.notification
	);
    /**
     * sending a simple test
     */
    $scope.test = function(notification, text, title, subtext) {
    	notificationResourceService.notification.task(notification.id, 'test', {'text': text, 'title': title, 'subtext': subtext}, function(data) {
   	    	toastService.info('notification ' + notification.name + '#' + notification.id + ' tested');
	    }, toastService.failure);
    }
    /**
     * loading
     */
    $scope.load = function() {
		$scope.combo = {
				types: notificationResourceService.types
		}

		/**
		 * get current notification
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.notification=update}, notificationResourceService.notification);
    	$log.info('notification-ctrl');
    }
}])
.factory('jarvisWidgetNotificationService', [ 'genericResourceService', function( genericResourceService) {
	return {
		notification: genericResourceService.crud(['notifications']),
	  	types: [
	           	   {
	          		   id: 'SLACK',
	          		   value:'notification.slack'
	          	   },
	          	   {
	          		   id: 'MAIL',
	          		   value:'notification.mail'
	          	   }
	        ]
	}
}])
.directive('jarvisNotifications', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notifications.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNotification', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notification-general.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisNotificationTest', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/notification/partials/jarvis-notification-test.html',
    link: function(scope, element, attrs) {
    }
  }
}]);
