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
			'snapshotResourceService',
			'blockResourceService',
			'configurationResourceService',
			'propertyResourceService',
			'viewResourceService',
			'triggerResourceService',
			'scenarioResourceService',
			'cronResourceService',
			'connectorResourceService',
			'commandResourceService',
			'pluginResourceService',
			'deviceResourceService',
	function(
			$q,
			$location,
			snapshotResourceService,
			blockResourceService,
			configurationResourceService,
			propertyResourceService,
			viewResourceService,
			triggerResourceService,
			scenarioResourceService,
			cronResourceService,
			connectorResourceService,
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
	    		deferred.resolve({elements: devices, title: "Devices", route: "/devices"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all plugins
		 */
		plugins: function() {
			var deferred = $q.defer();
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
	    		deferred.resolve({elements: plugins, title: "Plugins", route: "/plugins"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all commands
		 */
		commands: function() {
			var deferred = $q.defer();
			commandResourceService.command.findAll(function(commands) {
				_.each(commands, function(command) {
					command.ext = command.body;
					command.selectable = true;
					command.callback = function(node) {
						 $location.path("/commands/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: commands, title: "Commands", route: "/commands"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all snapshots
		 */
		snapshots: function() {
			var deferred = $q.defer();
			snapshotResourceService.snapshot.findAll(function(snapshots) {
				_.each(snapshots, function(snapshot) {
					snapshot.selectable = true;
					snapshot.callback = function(node) {
						 $location.path("/snapshots/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: snapshots, title: "Snapshots", route: "/snapshots"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all blocks
		 */
		blocks: function() {
			var deferred = $q.defer();
			blockResourceService.block.findAll(function(blocks) {
				_.each(blocks, function(block) {
					block.selectable = true;
					block.callback = function(node) {
						 $location.path("/blocks/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: blocks, title: "Blocks", route: "/blocks"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all configurations
		 */
		configurations: function() {
			var deferred = $q.defer();
			configurationResourceService.configuration.findAll(function(configurations) {
				_.each(configurations, function(configuration) {
					configuration.selectable = true;
					configuration.callback = function(node) {
						 $location.path("/configurations/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: configurations, title: "Configurations", route: "/configurations"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all properties
		 */
		properties: function() {
			var deferred = $q.defer();
			propertyResourceService.property.findAll(function(properties) {
				_.each(properties, function(property) {
					property.selectable = true;
					property.callback = function(node) {
						 $location.path("/properties/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: properties, title: "Properties", route: "/properties"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		views: function() {
			var deferred = $q.defer();
			viewResourceService.view.findAll(function(views) {
				_.each(views, function(view) {
					view.selectable = true;
					view.callback = function(node) {
						 $location.path("/views/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: views, title: "Views", route: "/views"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		triggers: function() {
			var deferred = $q.defer();
			triggerResourceService.trigger.findAll(function(triggers) {
				_.each(triggers, function(trigger) {
					trigger.selectable = true;
					trigger.callback = function(node) {
						 $location.path("/triggers/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: triggers, title: "Triggers", route: "/triggers"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all views
		 */
		scenarios: function() {
			var deferred = $q.defer();
			scenarioResourceService.scenario.findAll(function(scenarios) {
				_.each(scenarios, function(scenario) {
					scenario.selectable = true;
					scenario.callback = function(node) {
						 $location.path("/scenarios/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: scenarios, title: "Scenarios", route: "/scenarios"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all crons
		 */
		crons: function() {
			var deferred = $q.defer();
			cronResourceService.cron.findAll(function(crons) {
				_.each(crons, function(cron) {
					cron.selectable = true;
					cron.callback = function(node) {
						 $location.path("/crons/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: crons, title: "Crontab", route: "/crons"});
	    	});
	    	return deferred.promise;
		},
		/**
		 * find all crons
		 */
		connectors: function() {
			var deferred = $q.defer();
			connectorResourceService.connector.findAll(function(connectors) {
				_.each(connectors, function(connector) {
					connector.selectable = true;
					connector.callback = function(node) {
						 $location.path("/connectors/" + node.id);
			    	}
				});
	    		deferred.resolve({elements: connectors, title: "Connectors", route: "/connectors"});
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
		scope.elements = [];
		/**
		 * load devices
		 */
    	jarvisWidgetNavigatorService.devices().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load plugins
		 */
    	jarvisWidgetNavigatorService.plugins().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load commands
		 */
    	jarvisWidgetNavigatorService.commands().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load snapshots
		 */
    	jarvisWidgetNavigatorService.snapshots().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load blocks
		 */
    	jarvisWidgetNavigatorService.blocks().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load configurations
		 */
    	jarvisWidgetNavigatorService.configurations().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load properties
		 */
    	jarvisWidgetNavigatorService.properties().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load views
		 */
    	jarvisWidgetNavigatorService.views().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load triggers
		 */
    	jarvisWidgetNavigatorService.triggers().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load scenarios
		 */
    	jarvisWidgetNavigatorService.scenarios().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load crons
		 */
    	jarvisWidgetNavigatorService.crons().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
		/**
		 * load connectors
		 */
    	jarvisWidgetNavigatorService.connectors().then(function(result){
    		scope.elements.push({
		    	 name:result.title,
		    	 selectable : true,
				 callback : function(node) {
					 $location.path(result.route);
				 },
		    	 nodes:result.elements
		     });
    	});
    }
  }
}])
