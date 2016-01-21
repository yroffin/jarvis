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
.directive('jarvisPlugins', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugins.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-plugins');
    }
  }
})
.directive('jarvisPluginCommon', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-common.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-common');
	    }
	  }
})
.directive('jarvisPluginScript', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-script.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-script');
	    }
	  }
})
.directive('jarvisPluginRender', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-render.html',
	    link: function(scope, element, attrs) {
	    	$log.debug('jarvis-plugin-output');
	    }
	  }
})
.directive('jarvisIots', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iots.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iots');
    }
  }
})
.directive('jarvisIot', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot');
    }
  }
})
.directive('jarvisIotPlugin', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot-plugin.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot-plugin');
    }
  }
})
.directive('jarvisIotRender', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot-render.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-iot-render');
    }
  }
})
.directive('jarvisJobs', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-jobs.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-jobs');
    }
  }
})
.directive('jarvisJob', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-job.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-job');
    }
  }
})
.directive('jarvisCommands', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-commands.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-commands');
    }
  }
})
.directive('jarvisCommand', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command');
    }
  }
})
.directive('jarvisCommandInput', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-input.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-input');
    }
  }
})
.directive('jarvisCommandScript', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-script.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-script');
    }
  }
})
.directive('jarvisCommandOutput', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-output.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-command-output');
    }
  }
})
.directive('jarvisViews', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/views/jarvis-views.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-views');
    }
  }
})
.directive('jarvisView', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/views/jarvis-view.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-view');
    }
  }
})
.directive('jarvisScenarios', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-scenarios.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenarios');
    }
  }
})
.directive('jarvisScenario', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-scenario.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario');
    }
  }
})
.directive('jarvisScenarioBlocks', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-blocks');
    }
  }
})
.directive('jarvisScenarioBlock', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-block.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-block');
    }
  }
})
.directive('jarvisScenarioGraph', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-scenario-graph.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-scenario-graph');
    }
  }
})
.directive('jarvisBlocks', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/blocks/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-blocks');
    }
  }
})
.directive('jarvisBlock', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/blocks/jarvis-block.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block');
    }
  }
})
.directive('jarvisBlockThen', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/blocks/jarvis-block-then.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block-then');
    }
  }
})
.directive('jarvisBlockElse', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/blocks/jarvis-block-else.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-block-else');
    }
  }
})
.directive('jarvisHome', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-home.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-home');
    }
  }
})
.directive('jarvisCard', function ($log, $parse) {
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
})
.directive('jarvisTile', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile');
    }
  }
})
.directive('jarvisTileBoolean', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-boolean.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-boolean');
    }
  }
})
.directive('jarvisTileInt', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-int.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-int');
    }
  }
})
.directive('jarvisTilePercent', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-percent.html',
    link: function(scope, element, attrs) {
    	$log.debug('jarvis-tile-percent');
    }
  }
})
.directive('jarvisInlineTemplate', function ($log, $stateParams, $parse) {
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
})
