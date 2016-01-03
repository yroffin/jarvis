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
    templateUrl: '/ui/js/partials/widget/jarvis-plugins.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-plugins');
    }
  }
})
.directive('jarvisPluginCommon', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/jarvis-plugin-common.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-common');
	    }
	  }
})
.directive('jarvisPluginScript', function ($log, $stateParams) {
	  return {
	    restrict: 'E',
	    templateUrl: '/ui/js/partials/widget/jarvis-plugin-script.html',
	    link: function(scope, element, attrs) {
	    	$log.info('jarvis-plugin-script');
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
.directive('jarvisHome', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/jarvis-home.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-home');
    }
  }
})
.directive('jarvisBottomSheetIot', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/bottom/jarvis-bottom-sheet-iot.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-iot');
    }
  }
})
.directive('jarvisBottomSheetPlugin', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/bottom/jarvis-bottom-sheet-plugin.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-plugin');
    }
  }
})
.directive('jarvisBottomSheetCommand', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/bottom/jarvis-bottom-sheet-command.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-command');
    }
  }
})
.directive('jarvisBottomSheetView', function ($log, $stateParams) {
  return {
    restrict: 'E',
    templateUrl: '/ui/js/partials/widget/bottom/jarvis-bottom-sheet-view.html',
    link: function(scope, element, attrs) {
    	$log.info('jarvis-bottom-sheet-view');
    }
  }
})
