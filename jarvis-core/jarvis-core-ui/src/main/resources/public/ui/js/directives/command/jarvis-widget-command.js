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

/* Directives */

angular.module('jarvis.directives.command', ['JarvisApp.services'])
.controller('commandsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'jarvisWidgetCommandService',
	function($scope, $log, genericScopeService, jarvisWidgetCommandService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.commands = entities;
			},
			function() {
				return $scope.commands;
			},
			$scope, 
			'commands', 
			jarvisWidgetCommandService.command,
			{
    			name: "command name",
    			icon: "list"
    		}
	);
}])
.controller('commandCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'jarvisWidgetCommandService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, jarvisCommandService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'command', 
			'commands', 
			jarvisCommandService.command
	);
	/**
	 * declare links
	 */
	$scope.links = {
			notifications: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.notifications;
			},
			$scope.links.notifications,
			'notification',
			'notifications',
			jarvisCommandService.command, 
			jarvisCommandService.notifications, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * execute this command
     * @param command, the command to execute
     */
    $scope.execute = function(command) {
    	jarvisWidgetCommandService.command.task(command.id, 'execute', $scope.rawTestData, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' executed');
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * test this command
     * @param command, the command to execute
     */
    $scope.test = function(command) {
    	jarvisWidgetCommandService.command.task(command.id, 'test', $scope.rawTestData, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' tested');
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * transform command
     */
    $scope.pretty = function() {
    	$log.debug('pretty', $scope.rawinput);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * fix params
     */
    $scope.submit = function() {
    	$scope.rawTestData = angular.fromJson($scope.editTestData);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * clear params
     */
    $scope.clear = function() {
    	$scope.rawTestData = {"default":"todo"};
    	$scope.editTestData = angular.toJson($scope.rawTestData, true);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    }
    /**
     * loading
     */
    $scope.load = function() {
	    /**
	     * init part
	     */
		$scope.combo = {
				visibles: jarvisCommandService.bool,
				types: jarvisCommandService.types
		}
		/**
		 * input test
		 */
		$scope.clear();
		/**
		 * get current command
		 */
		$scope.commands = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.command=update}, jarvisCommandService.command);
	
		/**
		 * get all crontabs
		 */
    	$scope.notifications = [];
    	genericResourceService.scope.collections.findAll('notifications', $stateParams.id, $scope.notifications, jarvisCommandService.notifications);

		$log.info('command-ctrl');
    }
}])
.factory('jarvisWidgetCommandService', [ 'genericResourceService', function( genericResourceService) {
	return {
	  	command: genericResourceService.crud(['commands']),
	  	notifications : genericResourceService.links(['commands'], ['notifications']),
	  	bool: [
           	   {
           		   id: true,
           		   value:'common.true'
           	   },
           	   {
           		   id: false,
           		   value:'common.false'
           	   }
        ],
	  	types: [
           	   {
          		   id: 'SHELL',
          		   value:'command.shell'
          	   },
          	   {
          		   id: 'COMMAND',
          		   value:'command.single'
          	   },
          	   {
          		   id: 'GROOVY',
          		   value:'command.groovy'
          	   },
          	   {
          		   id: 'SSH',
          		   value:'command.ssh'
          	   },
          	   {
          		   id: 'ZWAY',
          		   value:'command.zway'
          	   }
        ]
	}
}])
/**
 * commands
 */
.directive('jarvisWidgetCommands', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/jarvis-widget-commands.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCommands', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-commands.html',
    link: function(scope, element, attrs) {
    }
  }
}])
/**
 * command
 */
.directive('jarvisWidgetCommand', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/jarvis-widget-command.html',
    link: function(scope, element, attrs) {
    }
  }
}])
.directive('jarvisCommand', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command');
    }
  }
}])
.directive('jarvisCommandNotification', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-notification.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-notification');
    }
  }
}])
.directive('jarvisCommandInput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-input.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-input');
    }
  }
}])
.directive('jarvisCommandScript', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-script.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-script');
    }
  }
}])
.directive('jarvisCommandOutput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/directives/command/partials/jarvis-command-output.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-output');
    }
  }
}]);

