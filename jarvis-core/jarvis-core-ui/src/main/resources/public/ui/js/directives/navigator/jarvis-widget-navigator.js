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

angular.module('jarvis.directives.paperjs', ['JarvisApp.services'])
.factory('jarvisWidgetNavigatorService', [
			'$q',
			'$location',
			'commandResourceService',
			'pluginResourceService',
			'deviceResourceService',
	function(
			$q,
			$location,
			commandResourceService,
			pluginResourceService,
			deviceResourceService) {
	return {
		/**
		 * find all devices
		 */
		devices: function() {
			var deferred = $q.defer();
			var devices = [];
	    	deviceResourceService.device.findAll(function(devices) {
				_.each(devices, function(device) {
					deviceResourceService.plugins.findAll(device.id,function(plugins) {
						device.desc = "";
						_.each(plugins, function(plugin) {
							device.desc += plugin.name;
						});
			    	});
					device.selectable = true;
					device.callback = function(node) {
						 $location.path("/devices/" + node.id);
			    	}
					device.ext = angular.toJson(angular.fromJson(device.parameters),true);
				});
	    		deferred.resolve(devices);
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all plugins
		 */
		plugins: function() {
			var deferred = $q.defer();
			var devices = [];
			pluginResourceService.scripts.findAll(function(plugins) {
				_.each(plugins, function(plugin) {
					pluginResourceService.commands.findAll(plugin.id,function(commands) {
						plugin.desc = "";
						_.each(commands, function(command) {
							plugin.desc += command.name;
						});
			    	});
					plugin.selectable = true;
					plugin.callback = function(node) {
						 $location.path("/plugins/scripts/" + node.id);
			    	}
				});
				deferred.resolve(plugins);
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all commands
		 */
		commands: function() {
			var deferred = $q.defer();
			var devices = [];
			commandResourceService.command.findAll(function(commands) {
				_.each(commands, function(command) {
					command.ext = command.body;
					command.selectable = true;
					command.callback = function(node) {
						 $location.path("/commands/" + node.id);
			    	}
				});
				deferred.resolve(commands);
	    	});
	    	return deferred.promise;
		}
	}
}])
.directive('jarvisWidgetNavigator', [
             '$log', '$location', '$stateParams', 'jarvisWidgetNavigatorService',
             function ($log, $location, $stateParams, jarvisWidgetNavigatorService) {
  return {
    restrict: 'A',
    templateUrl: '/ui/js/directives/navigator/jarvis-widget-navigator.html',
    link: function(scope, element, attrs) {
		scope.elementsPicker = [
		     {
		    	 name:"Root",
		    	 selectable : false,
		    	 nodes:[
		   		     {
				    	 name:"Devices",
				    	 selectable : true,
						 callback : function(node) {
							 $location.path("/devices");
						 },
				    	 nodes:[
				    	 ]
				     },
		   		     {
				    	 name:"Plugins",
				    	 selectable : false,
						 callback : function(node) {
							 $location.path("/devices");
						 },
				    	 nodes:[
				    	 ]
				     },
		   		     {
				    	 name:"Commandes",
				    	 selectable : false,
						 callback : function(node) {
							 $location.path("/commands");
						 },
				    	 nodes:[
				    	 ]
				     }
		    	 ]
		     }
	    ];
		/**
		 * click handler
		 */
    	scope.answer = function(node) {
    		node.callback(node);
    	}
		/**
		 * load devices
		 */
    	jarvisWidgetNavigatorService.devices().then(function(devices){
    		scope.elementsPicker[0].nodes[0].nodes = devices;
    	});
		/**
		 * load plugins
		 */
    	jarvisWidgetNavigatorService.plugins().then(function(plugins){
    		scope.elementsPicker[0].nodes[1].nodes = plugins;
    	});
		/**
		 * load commands
		 */
    	jarvisWidgetNavigatorService.commands().then(function(commands){
    		scope.elementsPicker[0].nodes[2].nodes = commands;
    	});
    }
  }
}])
