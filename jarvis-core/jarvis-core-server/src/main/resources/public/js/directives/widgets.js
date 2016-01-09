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
    	$log.info('jarvis-plugins');
    }
  }
})
.directive('jarvisPluginCommon', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-common.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-common');
	    }
	  }
})
.directive('jarvisPluginScript', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-script.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-script');
	    }
	  }
})
.directive('jarvisPluginRender', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/plugins/jarvis-plugin-render.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-output');
	    }
	  }
})
.directive('jarvisIots', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iots.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-iots');
    }
  }
})
.directive('jarvisIot', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-iot');
    }
  }
})
.directive('jarvisIotPlugin', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot-plugin.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-iot-plugin');
    }
  }
})
.directive('jarvisIotRender', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/iots/jarvis-iot-render.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-iot-render');
    }
  }
})
.directive('jarvisJobs', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-jobs.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-jobs');
    }
  }
})
.directive('jarvisJob', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-job.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-job');
    }
  }
})
.directive('jarvisCommands', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-commands.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-commands');
    }
  }
})
.directive('jarvisCommand', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-command');
    }
  }
})
.directive('jarvisCommandInput', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-input.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-command-input');
    }
  }
})
.directive('jarvisCommandScript', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-script.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-command-script');
    }
  }
})
.directive('jarvisCommandOutput', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/commands/jarvis-command-output.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-command-output');
    }
  }
})
.directive('jarvisViews', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-views.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-views');
    }
  }
})
.directive('jarvisView', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-view.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-view');
    }
  }
})
.directive('jarvisScenarios', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-scenarios.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-scenarios');
    }
  }
})
.directive('jarvisScenario', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-scenario.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-scenario');
    }
  }
})
.directive('jarvisBlocks', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-blocks.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-blocks');
    }
  }
})
.directive('jarvisBlock', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/scenarios/jarvis-block.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-block');
    }
  }
})
.directive('jarvisHome', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-home.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-home');
    }
  }
})
.directive('jarvisTile', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-tile');
    }
  }
})
.directive('jarvisTileBoolean', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-boolean.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-tile-boolean');
    }
  }
})
.directive('jarvisTileInt', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-int.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-tile-int');
    }
  }
})
.directive('jarvisTilePercent', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/tile/jarvis-tile-percent.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-tile-percent');
    }
  }
})
.directive('jarvisInlineTemplate', function ($log, $stateParams, $parse) {
  return {
    restrict: 'E',
    template: '<div ng-include="getJarvisInlineTemplateUrl()"></div>',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-inline-template iot.id', $parse(attrs.id)(scope));
    	scope.getJarvisInlineTemplateUrl = function() {
            return '/api/directives/html/iots/'+$parse(attrs.id)(scope);
        }
    }
  }
})
