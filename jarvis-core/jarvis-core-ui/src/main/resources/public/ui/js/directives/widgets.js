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

/* Directives */

angular.module('JarvisApp.directives.widgets', ['JarvisApp.services'])
.directive('jarvisPlugins', ['$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/plugins/jarvis-plugins.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-plugins');
    }
  }
}])
.directive('jarvisDefaultMenuBar', [ '$log', '$parse', function ($log, $parse) {
	return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/tools/jarvis-default-menu-bar.html',
	    require:"ngModel",
	    link: function($scope, elm, attrs, ngModel) {
	    	$scope.closeMenu = attrs.close;
	    	$scope.element = function () {
                return ngModel.$viewValue;
            }
	    }
	}
}])
.directive('jarvisPickElement', [ '$log', '$parse', 'genericPickerService', function ($log, $parse, genericPickerService) {
	return {
	    restrict: 'E',
	    template: '<md-button aria-label="menu" class="md-icon-button" ng-click="pickItemDialog($event)"><md-tooltip>{{pick.tooltips|translate}}</md-tooltip><md-icon md-font-icon="material-icons md-16">{{pick.icon}}</md-icon></md-button>',
	    scope: {},
	    link: function(scope, elm, attrs, ctrl) {
	    	scope.pick = {};
	    	scope.pick.tooltips = attrs.tooltips;
	    	scope.pick.icon = attrs.icon;
	    	scope.pick.tooltips = attrs.tooltips;
	    	scope.pick.ctrl = attrs.ctrl;
	    	scope.pick.callback = attrs.callback;
	        scope.pickItemDialog = function(ev, node) {
	        	return genericPickerService.pickers.nodes(ev, function(node) {
	        		$parse(scope.pick.callback)(scope.$parent)(node);
	        	}, function() {
	        		$log.debug('no picked element');
	        	},
	        	scope.pick.ctrl);
	        }
	    }
	}
}])
.directive('jarvisPluginCommon', [ '$log', '$stateParams', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/plugins/script/jarvis-plugin-general.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-common');
	    }
	  }
}])
.directive('jarvisPluginScript', [ '$log', '$stateParams', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/plugins/script/jarvis-plugin-script.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-script');
	    }
	  }
}])
.directive('typecommand', [ '$log', function ($log) {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
			ctrl.$validators.typecommand = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					// consider empty models to be valid
					return false;
				}
				if(modelValue == 'data' || modelValue == 'action') {
					return true;
				}
				// it is invalid
			    return false;
			};
	    }
	}
}])
.directive('naturecommand', [ '$log', function ($log) {
	return {
	    require: 'ngModel',
	    link: function(scope, elm, attrs, ctrl) {
			ctrl.$validators.naturecommand = function(modelValue, viewValue) {
				if (ctrl.$isEmpty(modelValue)) {
					// consider empty models to be valid
					return false;
				}
				if(modelValue == 'text' || modelValue == 'json' || modelValue == 'xml') {
					return true;
				}
				// it is invalid
			    return false;
			};
	    }
	}
}])
.directive('jarvisPluginRender', [ '$log', '$stateParams', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/plugins/script/jarvis-plugin-result.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-output');
	    }
	  }
}])
.directive('jarvisIots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/iots/jarvis-iots.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iots');
    }
  }
}])
.directive('jarvisIot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/iots/iot/jarvis-iot-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot-general');
    }
  }
}])
.directive('jarvisIotPlugin', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/iots/iot/jarvis-iot-plugin.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot-plugin');
    }
  }
}])
.directive('jarvisIotRender', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/iots/iot/jarvis-iot-render.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot-render');
    }
  }
}])
.directive('jarvisEvents', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/events/jarvis-events.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iots');
    }
  }
}])
.directive('jarvisTriggers', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/triggers/jarvis-triggers.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-triggers');
    }
  }
}])
.directive('jarvisTrigger', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/triggers/trigger/jarvis-trigger-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-trigger');
    }
  }
}])
.directive('jarvisTriggerCron', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/triggers/trigger/jarvis-trigger-cron.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-trigger-cron');
    }
  }
}])
.directive('jarvisCommands', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/commands/jarvis-commands.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-commands');
    }
  }
}])
.directive('jarvisCommand', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/commands/command/jarvis-command-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command');
    }
  }
}])
.directive('jarvisCommandInput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/commands/command/jarvis-command-input.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-input');
    }
  }
}])
.directive('jarvisCommandScript', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/commands/command/jarvis-command-script.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-script');
    }
  }
}])
.directive('jarvisCommandOutput', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/commands/command/jarvis-command-output.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-output');
    }
  }
}])
.directive('jarvisViews', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/views/jarvis-views.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-views');
    }
  }
}])
.directive('jarvisView', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/views/view/jarvis-view-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-view');
    }
  }
}])
.directive('jarvisConfigurations', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/configurations/jarvis-configurations.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-configurations');
    }
  }
}])
.directive('jarvisConfiguration', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/configurations/configuration/jarvis-configuration-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-configuration');
    }
  }
}])
.directive('jarvisProperties', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/properties/jarvis-properties.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-properties');
    }
  }
}])
.directive('jarvisProperty', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/properties/property/jarvis-property-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-property');
    }
  }
}])
.directive('jarvisConnectors', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/connectors/jarvis-connectors.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-connectors');
    }
  }
}])
.directive('jarvisConnectorGeneral', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/connectors/connector/jarvis-connector-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-connector-general');
    }
  }
}])
.directive('jarvisSnapshots', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/snapshots/jarvis-snapshots.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-snapshots');
    }
  }
}])
.directive('jarvisSnapshot', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/snapshots/snapshot/jarvis-snapshot-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-snapshot');
    }
  }
}])
.directive('jarvisCrons', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/crons/jarvis-crons.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-crons');
    }
  }
}])
.directive('jarvisCron', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/crons/cron/jarvis-cron-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-cron');
    }
  }
}])
.directive('jarvisScenarios', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/jarvis-scenarios.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenarios');
    }
  }
}])
.directive('jarvisScenario', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/scenario/jarvis-scenario-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario');
    }
  }
}])
.directive('jarvisScenarioBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/scenario/jarvis-scenario-blocks.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-blocks');
    }
  }
}])
.directive('jarvisScenarioBlock', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/scenario/jarvis-scenario-block.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-block');
    }
  }
}])
.directive('jarvisScenarioGraph', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/scenario/jarvis-scenario-graph.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-graph');
    }
  }
}])
.directive('jarvisScenarioConsole', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/scenarios/scenario/jarvis-scenario-console.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-console');
    }
  }
}])
.directive('jarvisBlocks', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/blocks/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-blocks');
    }
  }
}])
.directive('jarvisBlock', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/blocks/block/jarvis-block-general.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block');
    }
  }
}])
.directive('jarvisBlockThen', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/blocks/block/jarvis-block-then.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block-then');
    }
  }
}])
.directive('jarvisBlockElse', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/blocks/block/jarvis-block-else.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block-else');
    }
  }
}])
.directive('jarvisHome', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/home/jarvis-home.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-home');
    }
  }
}])
.directive('jarvisCard', [ '$log', '$parse', function ($log, $parse) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-card.html',
    scope: false,
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-card element', $parse(attrs.element)(scope));
    	$log.debug('jarvis-card state', $parse(attrs.state)(scope));
    	scope.element = $parse(attrs.element)(scope);
    	scope.state = $parse(attrs.state)(scope);
    }
  }
}])
.directive('jarvisTile', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile');
    }
  }
}])
.directive('jarvisTileBoolean', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-boolean.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-boolean');
    }
  }
}])
.directive('jarvisTileInt', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-int.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-int');
    }
  }
}])
.directive('jarvisTilePercent', [ '$log', '$stateParams', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-percent.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-percent');
    }
  }
}])
.directive('jarvisInlineTemplate', [ '$log', '$stateParams', '$parse', function ($log, $stateParams, $parse) {
  return {
    restrict: 'E',
    template: '<div ng-include="getJarvisInlineTemplateUrl()"></div>',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-inline-template id', $parse(attrs.id)(scope));
    	scope.getJarvisInlineTemplateUrl = function() {
    		/**
    		 * update scope
    		 */
        	scope.data = $parse(attrs.data)(scope);
            return '/api/directives/html/iots/'+$parse(attrs.id)(scope);
        }
    }
  }
}])
.directive('jarvisGauge', [ '$log', '$store', function ($log, $store) {
  return {
    restrict: 'AC',
    controller: [ '$scope', '$element', '$attrs', function($scope, $element, $attrs) {
    	$log.debug('jarvis-gauge', $attrs.id);
    	var opts = {
    			  fontSize: 10,
    			  lines: 10, // The number of lines to draw
    			  angle: 0.0, // The length of each line
    			  lineWidth: 0.32, // The line thickness
    			  pointer: {
    			    length: 0.9, // The radius of the inner circle
    			    strokeWidth: 0.035, // The rotation offset
    			    color: '#000000' // Fill color
    			  },
    			  limitMax: 'false',   // If true, the pointer will not go past the end of the gauge
    			  colorStart: '#6FADCF',   // Colors
    			  colorStop: '#8FC0DA',    // just experiment with them
    			  strokeColor: '#E0E0E0',   // to see which ones work best for you
    			  generateGradient: true
    			};
		$scope.gauge = new Gauge(document.getElementById($attrs.id)).setOptions(opts); // create sexy gauge!
		$scope.gauge.maxValue = 100; // set max gauge value
		$scope.gauge.animationSpeed = 10; // set animation speed (32 is default value)
		$scope.gauge.set(0); // set actual value
		
		// for the $watch
		$scope.systemIndicator = function() {
			if($store.collection == undefined) return 0;
			if($store.collection['SystemIndicator'] == undefined) return 0;
			if($store.collection['SystemIndicator']['1'] == undefined) return 0;
			return $store.collection['SystemIndicator']['1'];
		}
		
		$scope.$watch($scope.systemIndicator, function(newValue, oldValue, scope) {
			$scope.gauge.set(newValue.processCpuLoad*1000);
		});
    }]
  }
}])
