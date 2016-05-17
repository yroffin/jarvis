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

/**
 * Declare app level module which depends on filters, and services
 */

angular.module('JarvisApp', [
     'ngMaterial',
     'ngMdIcons',
     'ngSanitize',
     'restangular',
     'angular-websocket',
     'ui.router',
     'ui.router.router',
     'pascalprecht.translate',
     'ngCookies',
     'hljs',
     'ui.tree',
     'JarvisApp.websocket',
     'JarvisApp.config',
     'JarvisApp.theme',
     'JarvisApp.routes',
     /**
      * services
      */
     'JarvisApp.services',
     'JarvisApp.services.filter',
     'JarvisApp.services.generic',
     'JarvisApp.services.scope',
     'JarvisApp.services.plugin',
     'JarvisApp.services.command',
     'JarvisApp.services.scenario',
     'JarvisApp.services.snapshot',
     'JarvisApp.services.cron',
     'JarvisApp.services.view',
     'JarvisApp.services.configuration',
     'JarvisApp.services.property',
     'JarvisApp.services.connector',
     'JarvisApp.services.iot',
     'JarvisApp.services.event',
     'JarvisApp.services.trigger',
     'JarvisApp.services.block',
     'JarvisApp.directives',
     'JarvisApp.directives.widgets',
     /**
      * controllers
      */
     'JarvisApp.ctrl.plugins',
     'JarvisApp.ctrl.commands',
     'JarvisApp.ctrl.iots',
     'JarvisApp.ctrl.events',
     'JarvisApp.ctrl.triggers',
     'JarvisApp.ctrl.snapshots',
     'JarvisApp.ctrl.crons',
     'JarvisApp.ctrl.views',
     'JarvisApp.ctrl.configurations',
     'JarvisApp.ctrl.properties',
     'JarvisApp.ctrl.connectors',
     'JarvisApp.ctrl.scenarios',
     'JarvisApp.ctrl.blocks',
     'JarvisApp.ctrl.home'
]);
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

/**
 * blockResourceService
 */
angular.module('JarvisApp.services.block', []).factory('blockResourceService',
		[ '$log', 'Restangular', 'genericResourceService', 'iotResourceService', 'filterService',
		function($log, Restangular, genericResourceService, iotResourceService, filterService) {
  return {
	    block: genericResourceService.crud(['blocks']),
	    plugins: {
	    	"if": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_IF'),
		    "then": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_THEN'),
		    "else": genericResourceService.links(['blocks'], ['plugins','scripts'],'HREF_ELSE')
	    },
		blocks: {
			"then": genericResourceService.links(['blocks'], ['blocks'],'HREF_THEN'),
			"else": genericResourceService.links(['blocks'], ['blocks'],'HREF_ELSE')
		}
  }
}]);
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

angular.module('JarvisApp.ctrl.blocks', ['JarvisApp.services'])
.controller('blocksCtrl', 
		[ '$scope',
		  '$log',
		  'genericScopeService',
		  'genericPickerService',
		  'blockResourceService',
		  'toastService', 
	function(
			$scope,
			$log,
			genericScopeService,
			genericPickerService,
			blockResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.blocks = entities;
			},
			function() {
				return $scope.blocks;
			},
			$scope, 
			'blocks', 
			blockResourceService.block,
			{
    			name: "block name",
    			icon: "list",
    			parameter: "{}"
    		}
	);
}])
.controller('blockCtrl',
	[ '$scope',
	  '$log',
	  '$stateParams',
	  'genericScopeService',
	  'genericResourceService',
	  'genericPickerService',
	  'blockResourceService',
	  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			blockResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'block', 
			'blocks', 
			blockResourceService.block
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins:{
				"if": {},
				"then": {},
				"else": {}
			},
			blocks:{
				"then": {},
				"else": {}
			}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.if;
			},
			$scope.links.plugins.if,
			'block',
			'blocks',
			blockResourceService.block, 
			blockResourceService.plugins.if, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.then;
			},
			$scope.links.plugins.then,
			'block',
			'blocks',
			blockResourceService.block, 
			blockResourceService.plugins.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins.else;
			},
			$scope.links.plugins.else,
			'block',
			'blocks',
			blockResourceService.block, 
			blockResourceService.plugins.else, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.then;
			},
			$scope.links.blocks.then,
			'block',
			'blocks',
			blockResourceService.block, 
			blockResourceService.blocks.then, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks.else;
			},
			$scope.links.blocks.else,
			'block',
			'blocks',
			blockResourceService.block, 
			blockResourceService.blocks.else, 
			{'order':'1'},
			$stateParams.id
	);
    /**
     * task
     */
    $scope.test = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		blockResourceService.block.task(block.id, 'test', {}, function(data) {
       	    	$scope.testExpression = data == "true";
    	    }, toastService.failure);
    	}
    }
    /**
     * task
     */
    $scope.execute = function(block) {
    	if(block != undefined && block.id != undefined && block.id != '') {
    		blockResourceService.block.task(block.id, 'execute', {}, function(data) {
       	    	$log.debug('[BLOCK/test]', block, data);
       	    	$scope.testExpression = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.block=update}, blockResourceService.block);

		/**
		 * get all plugins
		 */
    	$scope.plugins = {
    			if:{},
    			then:{},
    			else:{}
    	};
    	$scope.plugins.if = [];
    	genericResourceService.scope.collections.findAll('plugins.if', $stateParams.id, $scope.plugins.if, blockResourceService.plugins.if);
    	$scope.plugins.then = [];
    	genericResourceService.scope.collections.findAll('plugins.then', $stateParams.id, $scope.plugins.then, blockResourceService.plugins.then);
    	$scope.plugins.else = [];
    	genericResourceService.scope.collections.findAll('plugins.else', $stateParams.id, $scope.plugins.else, blockResourceService.plugins.else);

		/**
		 * get all plugins
		 */
    	$scope.blocks = {
    			then:{},
    			else:{}
    	};
    	$scope.blocks.then = [];
    	genericResourceService.scope.collections.findAll('blocks.then', $stateParams.id, $scope.blocks.then, blockResourceService.blocks.then);
    	$scope.blocks.else = [];
    	genericResourceService.scope.collections.findAll('blocks.else', $stateParams.id, $scope.blocks.else, blockResourceService.blocks.else);

    	$log.debug('block-ctrl', $scope.scenario);
    }
}])
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

/**
 * commandResourceService
 */
angular.module('JarvisApp.services.command', []).factory('commandResourceService', 
		[ 'genericResourceService',
		  function(genericResourceService) {
  return {
	  	command: genericResourceService.crud(['commands'])
  }
}]);
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

angular.module('JarvisApp.ctrl.commands', ['JarvisApp.services'])
.controller('commandsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'commandResourceService',
	function($scope, $log, genericScopeService, commandResourceService){
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
			commandResourceService.command,
			{
    			name: "command name",
    			icon: "list"
    		}
	);
}])
.controller('commandCtrl',
		[ '$scope, $log, $stateParams, genericResourceService, genericScopeService, commandResourceService, toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, commandResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'command', 
			'commands', 
			commandResourceService.command
	);
    /**
     * execute this command
     * @param command, the command to execute
     */
    $scope.execute = function(command) {
    	commandResourceService.command.task(command.id, 'execute', $scope.rawTestData, function(data) {
   	    	toastService.info('command ' + command.name + '#' + command.id + ' executed');
   	    	$scope.output = angular.toJson(data, true);
	    }, toastService.failure);
    }
    /**
     * test this command
     * @param command, the command to execute
     */
    $scope.test = function(command) {
    	commandResourceService.command.task(command.id, 'test', $scope.rawTestData, function(data) {
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
				visibles: [
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
		              	   }
		      ]
		}
		/**
		 * input test
		 */
		$scope.clear();
		/**
		 * get current command
		 */
		$scope.commands = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.command=update}, commandResourceService.command);
	
		$log.info('command-ctrl');
    }
}])
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

/**
 * viewResourceService
 */
angular.module('JarvisApp.services.configuration', []).factory('configurationResourceService', 
		[ 'genericResourceService', function(genericResourceService) {
  return {
	  configuration : genericResourceService.crud(['configurations'])
  }
}]);
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

angular.module('JarvisApp.ctrl.configurations', ['JarvisApp.services'])
.controller('configurationsCtrl', 
		['$scope', '$log', 'genericScopeService', 'configurationResourceService',
	function($scope, $log, genericScopeService, configurationResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.configurations = entities;
			},
			function() {
				return $scope.configurations;
			},
			$scope, 
			'configurations', 
			configurationResourceService.configuration,
			{
    			name: "default",
    			opacity: "1",
    			backgroundUrl: "http://artroyalephotography.com/wp-content/uploads/2011/08/minimal-gray-to-white-gradient-wallpapers_33797_1920x1200-1024x640.jpg"
    		}
	);
}])
.controller('configurationCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'configurationResourceService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, configurationResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'configuration', 
			'configurations', 
			configurationResourceService.configuration);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current configuration
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.config=update;
    	}, configurationResourceService.configuration);

    	$log.info('configuration-ctrl', $scope.configurations);
    }
}])
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

/**
 * connectorResourceService
 */
angular.module('JarvisApp.services.connector', []).factory('connectorResourceService', 
		[ 'genericResourceService', function(genericResourceService) {
  return {
	  connector : genericResourceService.crud(['connectors'])
  }
}]);
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

angular.module('JarvisApp.ctrl.connectors', ['JarvisApp.services'])
.controller('connectorsCtrl', 
		['$scope', '$log', 'genericScopeService', 'connectorResourceService',
	function($scope, $log, genericScopeService, connectorResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.connectors = entities;
			},
			function() {
				return $scope.connectors;
			},
			$scope, 
			'connectors', 
			connectorResourceService.connector,
			{
    			name: "connector name",
    			icon: "settings_input_antenna",
    			isRenderer: false,
    			isSensor: false,
    			canAnswer: false
    		}
	);
}])
.controller('connectorCtrl',
		['$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'connectorResourceService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, connectorResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'connector', 
			'connectors', 
			connectorResourceService.connector);
    /**
     * verify this connector
     */
    $scope.ping = function(connector) {
    	connectorResourceService.connector.task(connector.id, 'ping', connector, function(data) {
    		/**
    		 * check data for ping result
    		 */
    		$scope.status = angular.toJson(data, true);
    	});
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current connector
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.connector=update;
    	}, connectorResourceService.connector);

    	$log.info('connector-ctrl', $scope.connectors);
    }
}])
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

/* Controllers */

angular.module('JarvisApp.config',[])
    .config(['$mdIconProvider', function($mdIconProvider) {
		var $log =  angular.injector(['ng']).get('$log');
		$log.info('$mdIconProvider', $mdIconProvider);
	}])
	.config(['$translateProvider', function($translateProvider){
		var $log =  angular.injector(['ng']).get('$log');
		$log.info('$translateProvider', $translateProvider);
	  
		// Register a loader for the static files
		// So, the module will search missing translation tables under the specified urls.
		// Those urls are [prefix][langKey][suffix].
		$translateProvider.useStaticFilesLoader({
			prefix: 'js/l10n/',
			suffix: '.json'
		});
		// Tell the module what language to use by default
		$translateProvider.preferredLanguage('fr_FR');
		$translateProvider.useSanitizeValueStrategy(null);
	}])
    .config(['RestangularProvider', function(RestangularProvider) {
		var $log =  angular.injector(['ng']).get('$log');
		$log.info('RestangularProvider', RestangularProvider);
		
		RestangularProvider.setBaseUrl('/api');
		RestangularProvider.setDefaultHeaders({ 'content-type': 'application/json' });
		/**
		 * request interceptor
		 */
	    RestangularProvider.setFullRequestInterceptor(function(element, operation, route, url, headers, params, httpConfig) {
	      return {
	        element: element,
	        params: params,
	        headers: headers,
	        httpConfig: httpConfig
	      };
	    });
	    RestangularProvider.setResponseExtractor(function(response) {
	    	if(angular.isObject(response)) {
	    	  var newResponse = response;
	    	  newResponse.originalElement = response;
	    	  return newResponse
	    	} else {
	    		return response;
	    	}
	    });
    }])
    /**
     * main controller
     */
    .controller('JarvisAppCtrl',
    		['$scope',
    		 '$log',
    		 '$store',
    		 '$http',
    		 '$mdDialog',
    		 '$mdSidenav',
    		 '$mdMedia',
    		 '$location',
    		 '$state',
    		 'genericPickerService',
    		 'toastService',
    		 'iotResourceService',
    		 'eventResourceService',
    		 'configurationResourceService',
    	function(
    			$scope,
    			$log,
    			$store,
    			$http,
    			$mdDialog,
    			$mdSidenav,
    			$mdMedia,
    			$location,
    			$state,
    			genericPickerService,
    			toastService,
    			iotResourceService,
    			eventResourceService,
    			configurationResourceService){
    	$log.info('JarvisAppCtrl');

    	/**
         * initialize jarvis configuration
         */
        $scope.config = {};
        
        $scope.media = $mdMedia('xs');
        $scope.$watch(function() { return $mdMedia('xs'); }, function(media) {
            if(media) $scope.media = 'xs';
        });
        $scope.$watch(function() { return $mdMedia('gt-xs'); }, function(media) {
            if(media) $scope.media = 'gt-xs';
        });
        $scope.$watch(function() { return $mdMedia('sm'); }, function(media) {
            if(media) $scope.media = 'sm';
        });
        $scope.$watch(function() { return $mdMedia('gt-sm'); }, function(media) {
            if(media) $scope.media = 'gt-sm';
        });
        $scope.$watch(function() { return $mdMedia('md'); }, function(media) {
            if(media) $scope.media = 'md';
        });
        $scope.$watch(function() { return $mdMedia('gt-md'); }, function(media) {
            if(media) $scope.media = 'gt-md';
        });
        $scope.$watch(function() { return $mdMedia('lg'); }, function(media) {
            if(media) $scope.media = 'lg';
        });
        $scope.$watch(function() { return $mdMedia('gt-lg'); }, function(media) {
            if(media) $scope.media = 'gt-lg';
        });
        $scope.$watch(function() { return $mdMedia('xl'); }, function(media) {
            if(media) $scope.media = 'xl';
        });
        $scope.$watch(function() { return $mdMedia('print'); }, function(media) {
            if(media) $scope.media = 'print';
        });

        /**
         * highlight JS
         */
        hljs.initHighlightingOnLoad();
        
        /**
         * default value
         */
        $scope.defaultValue = function(value, def) {
        	if(value === undefined) {
        		return def;
        	}
        	return value
        }
        
        /**
         * load settings
         */
        $scope.loadSettings = function() {
            configurationResourceService.configuration.findAll(function(data) {
            	var config = _.find(data, 'active');
            	if(config) {
                    $scope.config = config;
            	}
    	    }, toastService.failure);
        }

        /**
         * load it once
         */
        $scope.loadSettings();

        /**
         * save settings
         */
        $scope.saveSettings = function() {
            configurationResourceService.configuration.put($scope.config, function(data) {
            	$log.info("Updated", data);
    	    }, toastService.failure);
        }

        /**
         * close settings
         */
        $scope.closeSettings = function() {
            $mdDialog.hide();
        }

        $scope.settings = function(ev) {
        	var useFullScreen = ($mdMedia('sm') || $mdMedia('xs'));
        	$mdDialog.show({
        		  scope: $scope,
        		  preserveScope: true,
        	      templateUrl: 'js/partials/dialog/settingsDialog.tmpl.html',
        	      parent: angular.element(document.body),
        	      targetEvent: ev,
        	      clickOutsideToClose:true,
        	      fullscreen: useFullScreen
        	}).then(function() {
     		   		$log.warn("Saving settings");
        	   }, function() {
        		   	$log.warn("Cancel settings save");
        	   }
        	);
        }

        /**
         * create showdown service in global scope
         */
        $scope.markdown = new showdown.Converter({
        	'tables': 'true',
        	'ghCodeBlocks': 'true'
        });
        
        /**
         * helper
         * @param help key
         */
        $scope.helper = function(key) {
        	$log.debug('State', $state.current.name);
        	
        	/**
        	 * read raw resource (partial load)
        	 */
        	$http.get('js/helps/fr/'+$state.current.name+'.markdown').then(function(response) {
        		/**
        		 * render markdown to html
        		 */
        		var html = $scope.markdown.makeHtml(response.data);
            	$scope.help = {"content": html.replace(/<table>/g, '<table class="table table-striped table-bordered table-hover">')};
            	$log.debug('Help', $scope.help);
                $mdSidenav('right').toggle();
        	});
        };

        /**
         * toggle navbar
         * @param menuId
         */
        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
        };

        /**
         * load configuration
         */
        $scope.location = function(menuId, target) {
            $mdSidenav(menuId).toggle();
            $location.path(target);
        };

        /**
         * go to state
         */
        $scope.go = function(target,params) {
            $state.go(target,params);
        };

        /**
         * send an event
         * @param iot
         * @param value
         */
        $scope.emit = function(iot, value) {
        	$log.debug('JarvisAppCtrl::emit', iot, value, iot.trigger);
        	eventResourceService.event.post( 
        			{
        				trigger:iot.trigger,
		        		timestamp: (new Date()).toISOString(),
		        		fired: true,
		        		number:value
	        		}, function(data) {
		            	$log.debug('JarvisAppCtrl::emit', data);
		       	    	toastService.info('event ' + data.trigger + '#' + data.id + ' emitted');
	        		}, toastService.failure);
        };

        /**
    	 * on server side execute all action on this iot
    	 * @param iot
    	 */
    	$scope.execute = function(iot) {
    	 	iotResourceService.iot.task(iot.id, 'execute', {}, function(data) {
    	 		$log.debug('JarvisAppCtrl::execute', data);
    	 	    $scope.renderdata = data;
    	    }, toastService.failure);
    	}

    	/**
         * go to state
         */
        $scope.graph = function(event, anchor) {
	    	return genericPickerService.pickers.graph(
	    			event,
	    			anchor,
	    			function(node) {
	    				$log.debug('Go');
	    			}, function() {
	    				$log.debug('Abort');
	    			},
	    			'graphDialogCtrl'
	    	);
        }

        $log.info('JarvisAppCtrl configured');
    }])
	.controller('graphDialogCtrl',
			['$scope', '$log', '$mdDialog',
		function($scope, $log, $mdDialog) {
		$log.info('graphDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
	}])
	.controller('pickPluginDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickPluginDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Connected Objects",
		    	 selectable : false,
		    	 nodes:[]
		     },
		     {
		    	 name:"Plugin Scripts",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crudIot = genericResourceService.crud(['iots']);
		$scope.crudIot.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = false;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
		$scope.crudScript = genericResourceService.crud(['plugins','scripts']);
		$scope.crudScript.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[1].nodes.push(element);
					});
				},
				toastService.failure
		)
	}])
	.controller('pickIotDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickIotDialogCtrl');

		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Connected Objects",
		    	 selectable : false,
		    	 nodes:[]
		     },
		     {
		    	 name:"Plugin Scripts",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crudIot = genericResourceService.crud(['iots']);
		$scope.crudIot.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
		$scope.crudScript = genericResourceService.crud(['plugins','scripts']);
		$scope.crudScript.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = false;
				    	$scope.elementsPicker[1].nodes.push(element);
					});
				},
				toastService.failure
		)
	}])
	.controller('pickCommandDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickCommandDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Commands",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['commands']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickTriggerDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
		$log.info('pickTriggerDialogCtrl');
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
		$scope.elementsPicker = [
		     {
		    	 name:"Triggers",
		    	 selectable : false,
		    	 nodes:[]
		     }
	    ];
		$scope.crud = genericResourceService.crud(['triggers']);
		$scope.crud.findAll(
				function(elements) {
					_.each(elements, function(element) {
						element.selectable = true;
				    	$scope.elementsPicker[0].nodes.push(element);
					});
				},
				toastService.failure
		);
	}])
	.controller('pickBlockDialogCtrl',
			['$scope', '$log', '$mdDialog', 'genericResourceService', 'toastService',
			function($scope, $log, $mdDialog, genericResourceService, toastService) {
			$log.info('pickBlockDialogCtrl');
			$scope.hide = function() {
			   $mdDialog.hide();
			 };
			$scope.cancel = function() {
			  $mdDialog.cancel();
			};
			$scope.answer = function(answer) {
			  $mdDialog.hide(answer);
			};
			$scope.elementsPicker = [
			     {
			    	 name:"Blocks",
			    	 selectable : false,
			    	 nodes:[]
			     }
		    ];
			$scope.crudBlock = genericResourceService.crud(['blocks']);
			$scope.crudBlock.findAll(
					function(elements) {
						_.each(elements, function(element) {
							element.selectable = true;
					    	$scope.elementsPicker[0].nodes.push(element);
						});
					},
					toastService.failure
			);
	}])
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

/**
 * cronResourceService
 */
angular.module('JarvisApp.services.cron', []).factory('cronResourceService', 
		[ 'genericResourceService', function(genericResourceService) {
  return {
	  cron : genericResourceService.crud(['crons'])
  }
}]);
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

angular.module('JarvisApp.ctrl.crons', ['JarvisApp.services'])
.controller('cronsCtrl', 
		['$scope', '$log', 'genericScopeService', 'cronResourceService',
	function($scope, $log, genericScopeService, cronResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.crons = entities;
			},
			function() {
				return $scope.crons;
			},
			$scope, 
			'crons', 
			cronResourceService.cron,
			{
    			name: "cron name",
    			icon: "list",
    			cron: "* * * * *"
    		}
	);
}])
.controller('cronCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'cronResourceService', 'iotResourceService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, cronResourceService, iotResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'cron', 
			'crons', 
			cronResourceService.cron);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current cron
		 */
    	$scope.crons = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.cron=update}, cronResourceService.cron);

		$log.info('cron-ctrl', $scope.crons);
    }
}])
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

angular.module('JarvisApp.directives', [])
    .directive('appVersion', function(version) {
      return function(scope, elm, attrs) {
        elm.text(version);
      };
    })
	.directive('fileModel', function ($parse, $log) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var modelSetter = model.assign;
	            
	            element.bind('change', function(){
	                scope.$apply(function(){
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };
	})
	.directive('fileSelect', function ($window, $log) {
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        link: function (scope, el, attr, ctrl) {
	            var fileReader = new $window.FileReader();
	
	            fileReader.onload = function () {
	            	var base64 = fileReader.result.substr(fileReader.result.lastIndexOf(",")+1);
	            	$log.debug("result:",fileReader.result)
	            	$log.debug("result base64 value:",base64)
	                ctrl.$setViewValue(atob(base64));
	
	                if ('fileLoaded' in attr) {
	                    scope.$eval(attr['fileLoaded']);
	                }
	            };
	
	            fileReader.onprogress = function (event) {
	                if ('fileProgress' in attr) {
	                    scope.$eval(attr['fileProgress'], {'$total': event.total, '$loaded': event.loaded});
	                }
	            };
	
	            fileReader.onerror = function () {
	                if ('fileError' in attr) {
	                    scope.$eval(attr['fileError'], {'$error': fileReader.error});
	                }
	            };
	
	            var fileType = attr['fileSelect'];
	
	            el.bind('change', function (e) {
	                var fileName = e.target.files[0];
	
	                if (fileType === '' || fileType === 'text') {
	                    fileReader.readAsText(fileName, 'UTF-8');
	                } else if (fileType === 'data') {
	                    fileReader.readAsDataURL(fileName);
	                }
	            });
	        }
	    };
	});
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

angular.module('JarvisApp.services.event', [])
	.factory('eventResourceService', [ 'genericResourceService', function(genericResourceService) {
		return {
			event: genericResourceService.crud(['events'])
		}
	}]);
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

angular.module('JarvisApp.ctrl.events', ['JarvisApp.services'])
.controller('eventsCtrl', 
		['$scope', '$log', 'genericScopeService', 'genericResourceService', 'eventResourceService',
	function($scope, $log, genericScopeService, genericResourceService, eventResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.events = entities;
			},
			function() {
				return $scope.events;
			},
			$scope, 
			'events',
			eventResourceService.event
	);
	/**
	 * some crud
	 */
	$scope.crud = genericResourceService.crud(['events']);
}]);/* 
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

var myAppServices = angular.module('JarvisApp.services.filter', []);

/**
 * filterService
 */
myAppServices.factory('filterService', [ 'Restangular', function(Restangular) {
  var base = {
	  /**
	   * strip restangular object from context
	   */
	  plain: function(element) {
		  if(angular.isArray(element) || angular.isObject(element)) {
			  var element = Restangular.stripRestangular(element);
			  delete element.originalElement;
			  return element;
		  } else {
			  return element;
		  }
	  }
  }
  return base;
}]);
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

/* Filters */

angular.module('JarvisApp.filters', []).
    filter('interpolate', ['version', function(version) {
      return function(text) {
        return String(text).replace(/\%VERSION\%/mg, version);
      }
    }]);
(function(){var a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q={}.hasOwnProperty,r=function(a,b){function c(){this.constructor=a}for(var d in b)q.call(b,d)&&(a[d]=b[d]);return c.prototype=b.prototype,a.prototype=new c,a.__super__=b.prototype,a};!function(){var a,b,c,d,e,f,g;for(g=["ms","moz","webkit","o"],c=0,e=g.length;e>c&&(f=g[c],!window.requestAnimationFrame);c++)window.requestAnimationFrame=window[f+"RequestAnimationFrame"],window.cancelAnimationFrame=window[f+"CancelAnimationFrame"]||window[f+"CancelRequestAnimationFrame"];return a=null,d=0,b={},requestAnimationFrame?window.cancelAnimationFrame?void 0:(a=window.requestAnimationFrame,window.requestAnimationFrame=function(c,e){var f;return f=++d,a(function(){return b[f]?void 0:c()},e),f},window.cancelAnimationFrame=function(a){return b[a]=!0}):(window.requestAnimationFrame=function(a,b){var c,d,e,f;return c=(new Date).getTime(),f=Math.max(0,16-(c-e)),d=window.setTimeout(function(){return a(c+f)},f),e=c+f,d},window.cancelAnimationFrame=function(a){return clearTimeout(a)})}(),String.prototype.hashCode=function(){var a,b,c,d,e;if(b=0,0===this.length)return b;for(c=d=0,e=this.length;e>=0?e>d:d>e;c=e>=0?++d:--d)a=this.charCodeAt(c),b=(b<<5)-b+a,b&=b;return b},o=function(a){var b,c;for(b=Math.floor(a/3600),c=Math.floor((a-3600*b)/60),a-=3600*b+60*c,a+="",c+="";c.length<2;)c="0"+c;for(;a.length<2;)a="0"+a;return b=b?b+":":"",b+c+":"+a},m=function(a){return k(a.toFixed(0))},p=function(a,b){var c,d;for(c in b)q.call(b,c)&&(d=b[c],a[c]=d);return a},n=function(a,b){var c,d,e;d={};for(c in a)q.call(a,c)&&(e=a[c],d[c]=e);for(c in b)q.call(b,c)&&(e=b[c],d[c]=e);return d},k=function(a){var b,c,d,e;for(a+="",c=a.split("."),d=c[0],e="",c.length>1&&(e="."+c[1]),b=/(\d+)(\d{3})/;b.test(d);)d=d.replace(b,"$1,$2");return d+e},l=function(a){return"#"===a.charAt(0)?a.substring(1,7):a},j=function(){function a(a,b){null==a&&(a=!0),this.clear=null!=b?b:!0,a&&AnimationUpdater.add(this)}return a.prototype.animationSpeed=32,a.prototype.update=function(a){var b;return null==a&&(a=!1),a||this.displayedValue!==this.value?(this.ctx&&this.clear&&this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height),b=this.value-this.displayedValue,Math.abs(b/this.animationSpeed)<=.001?this.displayedValue=this.value:this.displayedValue=this.displayedValue+b/this.animationSpeed,this.render(),!0):!1},a}(),e=function(a){function b(){return b.__super__.constructor.apply(this,arguments)}return r(b,a),b.prototype.displayScale=1,b.prototype.setTextField=function(a){return this.textField=a instanceof i?a:new i(a)},b.prototype.setMinValue=function(a,b){var c,d,e,f,g;if(this.minValue=a,null==b&&(b=!0),b){for(this.displayedValue=this.minValue,f=this.gp||[],g=[],d=0,e=f.length;e>d;d++)c=f[d],g.push(c.displayedValue=this.minValue);return g}},b.prototype.setOptions=function(a){return null==a&&(a=null),this.options=n(this.options,a),this.textField&&(this.textField.el.style.fontSize=a.fontSize+"px"),this.options.angle>.5&&(this.gauge.options.angle=.5),this.configDisplayScale(),this},b.prototype.configDisplayScale=function(){var a,b,c,d,e;return d=this.displayScale,this.options.highDpiSupport===!1?delete this.displayScale:(b=window.devicePixelRatio||1,a=this.ctx.webkitBackingStorePixelRatio||this.ctx.mozBackingStorePixelRatio||this.ctx.msBackingStorePixelRatio||this.ctx.oBackingStorePixelRatio||this.ctx.backingStorePixelRatio||1,this.displayScale=b/a),this.displayScale!==d&&(e=this.canvas.G__width||this.canvas.width,c=this.canvas.G__height||this.canvas.height,this.canvas.width=e*this.displayScale,this.canvas.height=c*this.displayScale,this.canvas.style.width=e+"px",this.canvas.style.height=c+"px",this.canvas.G__width=e,this.canvas.G__height=c),this},b}(j),i=function(){function a(a){this.el=a}return a.prototype.render=function(a){return this.el.innerHTML=m(a.displayedValue)},a}(),a=function(a){function b(a,b){this.elem=a,this.text=null!=b?b:!1,this.value=1*this.elem.innerHTML,this.text&&(this.value=0)}return r(b,a),b.prototype.displayedValue=0,b.prototype.value=0,b.prototype.setVal=function(a){return this.value=1*a},b.prototype.render=function(){var a;return a=this.text?o(this.displayedValue.toFixed(0)):k(m(this.displayedValue)),this.elem.innerHTML=a},b}(j),b={create:function(b){var c,d,e,f;for(f=[],d=0,e=b.length;e>d;d++)c=b[d],f.push(new a(c));return f}},h=function(a){function b(a){this.gauge=a,this.ctx=this.gauge.ctx,this.canvas=this.gauge.canvas,b.__super__.constructor.call(this,!1,!1),this.setOptions()}return r(b,a),b.prototype.displayedValue=0,b.prototype.value=0,b.prototype.options={strokeWidth:.035,length:.1,color:"#000000"},b.prototype.setOptions=function(a){return null==a&&(a=null),p(this.options,a),this.length=this.canvas.height*this.options.length,this.strokeWidth=this.canvas.height*this.options.strokeWidth,this.maxValue=this.gauge.maxValue,this.minValue=this.gauge.minValue,this.displayedValue=this.gauge.minValue,this.animationSpeed=this.gauge.animationSpeed,this.options.angle=this.gauge.options.angle},b.prototype.render=function(){var a,b,c,d,e,f,g,h,i;return a=this.gauge.getAngle.call(this,this.displayedValue),b=this.canvas.width/2,c=.9*this.canvas.height,h=Math.round(b+this.length*Math.cos(a)),i=Math.round(c+this.length*Math.sin(a)),f=Math.round(b+this.strokeWidth*Math.cos(a-Math.PI/2)),g=Math.round(c+this.strokeWidth*Math.sin(a-Math.PI/2)),d=Math.round(b+this.strokeWidth*Math.cos(a+Math.PI/2)),e=Math.round(c+this.strokeWidth*Math.sin(a+Math.PI/2)),this.ctx.fillStyle=this.options.color,this.ctx.beginPath(),this.ctx.arc(b,c,this.strokeWidth,0,2*Math.PI,!0),this.ctx.fill(),this.ctx.beginPath(),this.ctx.moveTo(f,g),this.ctx.lineTo(h,i),this.ctx.lineTo(d,e),this.ctx.fill()},b}(j),c=function(){function a(a){this.elem=a}return a.prototype.updateValues=function(a){return this.value=a[0],this.maxValue=a[1],this.avgValue=a[2],this.render()},a.prototype.render=function(){var a,b;return this.textField&&this.textField.text(m(this.value)),0===this.maxValue&&(this.maxValue=2*this.avgValue),b=this.value/this.maxValue*100,a=this.avgValue/this.maxValue*100,$(".bar-value",this.elem).css({width:b+"%"}),$(".typical-value",this.elem).css({width:a+"%"})},a}(),g=function(a){function b(a){this.canvas=a,b.__super__.constructor.call(this),this.percentColors=null,"undefined"!=typeof G_vmlCanvasManager&&(this.canvas=window.G_vmlCanvasManager.initElement(this.canvas)),this.ctx=this.canvas.getContext("2d"),this.gp=[new h(this)],this.setOptions(),this.render()}return r(b,a),b.prototype.elem=null,b.prototype.value=[20],b.prototype.maxValue=80,b.prototype.minValue=0,b.prototype.displayedAngle=0,b.prototype.displayedValue=0,b.prototype.lineWidth=40,b.prototype.paddingBottom=.1,b.prototype.percentColors=null,b.prototype.options={colorStart:"#6fadcf",colorStop:void 0,gradientType:0,strokeColor:"#e0e0e0",pointer:{length:.8,strokeWidth:.035},angle:.15,lineWidth:.44,fontSize:40,limitMax:!1},b.prototype.setOptions=function(a){var c,d,e,f;for(null==a&&(a=null),b.__super__.setOptions.call(this,a),this.configPercentColors(),this.lineWidth=this.canvas.height*(1-this.paddingBottom)*this.options.lineWidth,this.radius=this.canvas.height*(1-this.paddingBottom)-this.lineWidth,this.ctx.clearRect(0,0,this.canvas.width,this.canvas.height),this.render(),f=this.gp,d=0,e=f.length;e>d;d++)c=f[d],c.setOptions(this.options.pointer),c.render();return this},b.prototype.configPercentColors=function(){var a,b,c,d,e,f,g;if(this.percentColors=null,void 0!==this.options.percentColors){for(this.percentColors=new Array,f=[],c=d=0,e=this.options.percentColors.length-1;e>=0?e>=d:d>=e;c=e>=0?++d:--d)g=parseInt(l(this.options.percentColors[c][1]).substring(0,2),16),b=parseInt(l(this.options.percentColors[c][1]).substring(2,4),16),a=parseInt(l(this.options.percentColors[c][1]).substring(4,6),16),f.push(this.percentColors[c]={pct:this.options.percentColors[c][0],color:{r:g,g:b,b:a}});return f}},b.prototype.set=function(a){var b,c,d,e,f,g,i;if(this.displayedValue=this.minValue,a instanceof Array||(a=[a]),a.length>this.gp.length)for(b=c=0,g=a.length-this.gp.length;g>=0?g>c:c>g;b=g>=0?++c:--c)this.gp.push(new h(this));for(b=0,f=!1,d=0,e=a.length;e>d;d++)i=a[d],i>this.maxValue&&(this.maxValue=1.1*this.value,f=!0),this.gp[b].value=i,this.gp[b++].setOptions({maxValue:this.maxValue,angle:this.options.angle});return this.value=a[a.length-1],f&&this.options.limitMax?void 0:AnimationUpdater.run()},b.prototype.getAngle=function(a){return(1+this.options.angle)*Math.PI+(a-this.minValue)/(this.maxValue-this.minValue)*(1-2*this.options.angle)*Math.PI},b.prototype.getColorForPercentage=function(a,b){var c,d,e,f,g,h,i;if(0===a)c=this.percentColors[0].color;else for(c=this.percentColors[this.percentColors.length-1].color,e=f=0,h=this.percentColors.length-1;h>=0?h>=f:f>=h;e=h>=0?++f:--f)if(a<=this.percentColors[e].pct){b===!0?(i=this.percentColors[e-1],d=this.percentColors[e],g=(a-i.pct)/(d.pct-i.pct),c={r:Math.floor(i.color.r*(1-g)+d.color.r*g),g:Math.floor(i.color.g*(1-g)+d.color.g*g),b:Math.floor(i.color.b*(1-g)+d.color.b*g)}):c=this.percentColors[e].color;break}return"rgb("+[c.r,c.g,c.b].join(",")+")"},b.prototype.getColorForValue=function(a,b){var c;return c=(a-this.minValue)/(this.maxValue-this.minValue),this.getColorForPercentage(c,b)},b.prototype.render=function(){var a,b,c,d,e,f,g,h,i;for(i=this.canvas.width/2,d=this.canvas.height*(1-this.paddingBottom),a=this.getAngle(this.displayedValue),this.textField&&this.textField.render(this),this.ctx.lineCap="butt",void 0!==this.options.customFillStyle?b=this.options.customFillStyle(this):null!==this.percentColors?b=this.getColorForValue(this.displayedValue,!0):void 0!==this.options.colorStop?(b=0===this.options.gradientType?this.ctx.createRadialGradient(i,d,9,i,d,70):this.ctx.createLinearGradient(0,0,i,0),b.addColorStop(0,this.options.colorStart),b.addColorStop(1,this.options.colorStop)):b=this.options.colorStart,this.ctx.strokeStyle=b,this.ctx.beginPath(),this.ctx.arc(i,d,this.radius,(1+this.options.angle)*Math.PI,a,!1),this.ctx.lineWidth=this.lineWidth,this.ctx.stroke(),this.ctx.strokeStyle=this.options.strokeColor,this.ctx.beginPath(),this.ctx.arc(i,d,this.radius,a,(2-this.options.angle)*Math.PI,!1),this.ctx.stroke(),g=this.gp,h=[],e=0,f=g.length;f>e;e++)c=g[e],h.push(c.update(!0));return h},b}(e),d=function(a){function b(a){this.canvas=a,b.__super__.constructor.call(this),"undefined"!=typeof G_vmlCanvasManager&&(this.canvas=window.G_vmlCanvasManager.initElement(this.canvas)),this.ctx=this.canvas.getContext("2d"),this.setOptions(),this.render()}return r(b,a),b.prototype.lineWidth=15,b.prototype.displayedValue=0,b.prototype.value=33,b.prototype.maxValue=80,b.prototype.minValue=0,b.prototype.options={lineWidth:.1,colorStart:"#6f6ea0",colorStop:"#c0c0db",strokeColor:"#eeeeee",shadowColor:"#d5d5d5",angle:.35},b.prototype.getAngle=function(a){return(1-this.options.angle)*Math.PI+(a-this.minValue)/(this.maxValue-this.minValue)*(2+this.options.angle-(1-this.options.angle))*Math.PI},b.prototype.setOptions=function(a){return null==a&&(a=null),b.__super__.setOptions.call(this,a),this.lineWidth=this.canvas.height*this.options.lineWidth,this.radius=this.canvas.height/2-this.lineWidth/2,this},b.prototype.set=function(a){return this.value=a,this.value>this.maxValue&&(this.maxValue=1.1*this.value),AnimationUpdater.run()},b.prototype.render=function(){var a,b,c,d,e,f;return a=this.getAngle(this.displayedValue),f=this.canvas.width/2,c=this.canvas.height/2,this.textField&&this.textField.render(this),b=this.ctx.createRadialGradient(f,c,39,f,c,70),b.addColorStop(0,this.options.colorStart),b.addColorStop(1,this.options.colorStop),d=this.radius-this.lineWidth/2,e=this.radius+this.lineWidth/2,this.ctx.strokeStyle=this.options.strokeColor,this.ctx.beginPath(),this.ctx.arc(f,c,this.radius,(1-this.options.angle)*Math.PI,(2+this.options.angle)*Math.PI,!1),this.ctx.lineWidth=this.lineWidth,this.ctx.lineCap="round",this.ctx.stroke(),this.ctx.strokeStyle=b,this.ctx.beginPath(),this.ctx.arc(f,c,this.radius,(1-this.options.angle)*Math.PI,a,!1),this.ctx.stroke()},b}(e),f=function(a){function b(){return b.__super__.constructor.apply(this,arguments)}return r(b,a),b.prototype.strokeGradient=function(a,b,c,d){var e;return e=this.ctx.createRadialGradient(a,b,c,a,b,d),e.addColorStop(0,this.options.shadowColor),e.addColorStop(.12,this.options._orgStrokeColor),e.addColorStop(.88,this.options._orgStrokeColor),e.addColorStop(1,this.options.shadowColor),e},b.prototype.setOptions=function(a){var c,d,e,f;return null==a&&(a=null),b.__super__.setOptions.call(this,a),f=this.canvas.width/2,c=this.canvas.height/2,d=this.radius-this.lineWidth/2,e=this.radius+this.lineWidth/2,this.options._orgStrokeColor=this.options.strokeColor,this.options.strokeColor=this.strokeGradient(f,c,d,e),this},b}(d),window.AnimationUpdater={elements:[],animId:null,addAll:function(a){var b,c,d,e;for(e=[],c=0,d=a.length;d>c;c++)b=a[c],e.push(AnimationUpdater.elements.push(b));return e},add:function(a){return AnimationUpdater.elements.push(a)},run:function(){var a,b,c,d,e;for(a=!0,e=AnimationUpdater.elements,c=0,d=e.length;d>c;c++)b=e[c],b.update()&&(a=!1);return a?cancelAnimationFrame(AnimationUpdater.animId):AnimationUpdater.animId=requestAnimationFrame(AnimationUpdater.run)}},"function"==typeof window.define&&null!=window.define.amd?define(function(){return{Gauge:g,Donut:f,BaseDonut:d,TextRenderer:i}}):"undefined"!=typeof module&&null!=module.exports?module.exports={Gauge:g,Donut:f,BaseDonut:d,TextRenderer:i}:(window.Gauge=g,window.Donut=f,window.BaseDonut=d,window.TextRenderer=i)}).call(this);/* 
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

/**
 * blockResourceService
 */
angular.module('JarvisApp.services.generic', ['JarvisApp.services.filter'])
  .factory('genericResourceService', [ '$log', 'Restangular', 'filterService', 'toastService', function($log, Restangular, filterService, toastService) {
  $log.info('genericResourceService');
  var resources = {
        /**
		 * find all elements
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(path, callback, failure) {
			var handler = function(elements) {
                var results = [];
            	_.forEach(elements, function(element) {
            		results.push(filterService.plain(element));
                });
				$log.debug("[FIND]",path, results);
				callback(results);
			}
			if(path.length == 1) {
				Restangular.all(path[0]).getList().then(
						handler,
						function(errors){
							failure(errors);
				});
			} else {
				Restangular.all(path[0]).all(path[1]).getList().then(
						handler,
						function(errors){
							failure(errors);
				});
			}
		},
        /**
		 * find one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		get: function(path, id, callback, failure) {
			var handler = function(toget) {
				var filtered = filterService.plain(toget);
				$log.debug("[GET]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one(path[0], id).get().then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all(path[0]).one(path[1], id).get().then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * delete one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(path, id, callback, failure) {
			var handler = function(todelete) {
				var filtered = filterService.plain(todelete);
				$log.debug("[DELETE]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one(path[0], id).remove().then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all(path[0]).one(path[1], id).remove().then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * update one element
		 * @param element, previous loaded element
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(path, element, callback, failure) {
			var handler = function(tosave) {
				var filtered = filterService.plain(tosave);
				$log.debug("[PUT]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.one(path[0], element.id).customPUT(element).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all(path[0]).one(path[1], element.id).customPUT(element).then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * create one element
		 * @param element, element to create
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(path, element, callback, failure) {
			var handler = function(topost) {
				var filtered = filterService.plain(topost);
				$log.debug("[POST]",path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.all(path[0]).post(element).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all(path[0]).all(path[1]).post(element).then(handler,function(errors){failure(errors);});
			}
		},
        /**
		 * execute task
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		task: function(path, id, task, args, callback, failure) {
			var handler = function(totask) {
				var filtered = filterService.plain(totask);
				$log.debug("[TASK]",totask,path,filtered);
				if(callback != undefined) callback(filtered);
			}
			if(path.length == 1) {
				Restangular.all(path[0]).one(id).customPOST(args,'', {'task':task}).then(handler,function(errors){failure(errors);});
			} else {
				Restangular.all(path[0]).all(path[1]).one(id).customPOST(args,'', {'task':task}).then(handler,function(errors){failure(errors);});
			}
		}
  };
  /**
   * crud operation
   */
  var crud = function(path) {
	return {
	  	/**
		 * find all elements
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(callback, failure) {
			return resources.findAll(path, callback, failure);
		},
		/**
		 * find one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		get: function(id, callback, failure) {
			return resources.get(path, id, callback, failure);
		},
		/**
		 * delete one element
		 * @param id, element id
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(id, callback, failure) {
			return resources.delete(path, id, callback, failure);
		},
		/**
		 * update one element
		 * @param element, previous loaded element
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(element, callback, failure) {
			return resources.put(path, element, callback, failure);
		},
		/**
		 * create one element
		 * @param element, element to create
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(element, callback, failure) {
			return resources.post(path, element, callback, failure);
		},
        /**
		 * execute task
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		task: function(id, task, args, callback, failure) {
			return resources.task(path, id, task, args, callback, failure);
		}
	 }
  }
  var links = {
			/**
			 * find all links
			 * @param api, api name
			 * @param path, resource path
			 * @param owner
			 * @param callback
			 * @param failure
			 */
			findAll: function(api, path, relation, id, callback, failure) {
				var handler = function(hrefs) {
					var results = [];
	            	_.forEach(hrefs, function(href) {
	            		results.push(filterService.plain(href));
	            	});
					$log.debug("[FIND/L/"+relation+"]",api, path, id, results);
	            	callback(results);
				};
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one(api[0], id).all(path[0]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], id).all(path[0]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one(api[0], id).all(path[0]).all(path[1]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], id).all(path[0]).all(path[1]).getList({'href':relation}).then(handler,function(errors){failure(errors);});
					}
				}
			},
	        /**
			 * put link
			 */
	        post: function(api, path, relation, owner, child, properties, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[POST/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
				if(properties != undefined) {
					properties.href = relation;
				}
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one(api[0], owner).one(path[0],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).one(path[0],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one(api[0], owner).all(path[0]).one(path[1],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).all(path[0]).one(path[1],child).customPOST(properties).then(handler,function(errors){failure(errors);});
					}
				}
	        },
	        /**
			 * put link
			 */
	        put: function(api, path, relation, owner, child, instance, properties, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[PUT/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
	        	var p = {};
	        	if(properties === undefined) {
	        		p = {};
	        	} else {
	        		p = properties;
	        	}
				p.href = relation;
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one(api[0], owner).one(path[0],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).one(path[0],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one(api[0], owner).all(path[0]).one(path[1],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).all(path[0]).one(path[1],child).one(instance).customPUT(p).then(handler,function(errors){failure(errors);});
					}
				}
	        },
	        /**
			 * delete link
			 */
			delete: function(api, path, relation, owner, child, instance, callback, failure) {
				var handler = function(href) {
					var filtered = filterService.plain(href);
					$log.debug("[DELETE/L]",api,filtered);
					if(callback != undefined) callback(filtered);
				};
				if(path.length == 1) {
					if(api.length == 1) {
						Restangular.one(api[0], owner).one(path[0], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).one(path[0], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					}
				} else {
					if(api.length == 1) {
						Restangular.one(api[0], owner).all(path[0]).one(path[1], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					} else {
						Restangular.all(api[0]).one(api[1], owner).all(path[0]).one(path[1], child).remove({'instance':instance, 'href':relation}).then(handler,function(errors){failure(errors);});
					}
				}
			}
  };
  /**
   * crud operation on links
   */
  var crudLinks = function(api, path, relation) {
	if(relation === undefined) {
		relation = "HREF";
	}
	return {
	  	/**
		 * find all links
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		findAll: function(id, callback, failure) {
			return links.findAll(api, path, relation, id, callback, failure);
		},
		/**
		 * delete one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		delete: function(owner, child, instance, callback, failure) {
			return links.delete(api, path, relation, owner, child, instance, callback, failure);
		},
		/**
		 * update one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		put: function(owner, child, instance, properties, callback, failure) {
			return links.put(api, path, relation, owner, child, instance, properties, callback, failure);
		},
		/**
		 * create one link
		 * @param callback, callback function in success case
		 * @param failure, callback function in failure case
		 */
		post: function(owner, child, properties, callback, failure) {
			return links.post(api, path, relation, owner, child, properties, callback, failure);
		}
	 }
  }
  /**
   * crud operation on links
   */
  var scopeCrud = {
	   	new : function(name, elements, update, service, callback) {
	        /**
	         * create this elements
	         */
	   		service.post(update, function(data) {
	                $log.debug(name + ' ' + data.name + '#' + data.id +' created');
	                elements.push(data);
	                if(callback) {
	                	callback(data);
	                }
	        }, toastService.failure);
	    },
	    remove : function(go, name, todelete, service) {
	    	service.delete(todelete.id, function(element) {
	        	toastService.info(name + ' ' + todelete.name + '#' + todelete.id + ' removed');
	        	go();
	        }, toastService.failure);
	    },
	    save : function(name, toput, service) {
	    	service.put(toput, function(element) {
	        	toastService.info(name + ' ' + toput.name + '#' + toput.id + ' updated');
	        }, toastService.failure);
	    },
	    duplicate : function(go, name, toduplicate, service) {
	    	service.post(toduplicate, function(element) {
	        	toastService.info(name + ' ' + toduplicate.name + '#' + toduplicate.id + ' duplicated');
	        	go();
	        }, toastService.failure);
	    },
	    addLink : function(id, link, properties, service, links) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.post(id, link.id, properties, function(found) {
		        	$log.debug('addLink', found);
		        	links.push(found);
		  	    }, toastService.failure);
		  	}
	    },
	    updateLink : function(id, link, service) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.put(id, link.id, link.instance, link.extended, function(found) {
		        	$log.debug('updateLink - before', link);
		        	$log.debug('updateLink - after', found);
		  	    }, toastService.failure);
		  	}
	    },
	    removeLink : function(id, link, service, links) {
		  	if(link != undefined && link.id != undefined && link.id != '') {
		  		service.delete(id, link.id, link.instance, function(found) {
		  			var toremove = link.instance;
		  			_.remove(links, function(element) {
		  				return element.instance == toremove;
		  			});
		        	$log.debug('removeLink - before', link);
		  	    }, toastService.failure);
		  	}
	    },
	    findAllIdName : function(name, list, service) {
			$log.debug('loading for select ', name, list);
	    	service.findAll(function(data) {
		    	_.forEach(data, function(element) {
		            /**
		             * add this element
		             */
		    		list.push({
		            	'id':element.id,
		            	'name':element.name
		            });
		        });
		    }, toastService.failure);
	    },
	    findAll : function(name, id, list, service, callback) {
			$log.debug('loading ', name, service);
			list.splice(0,list.length)
	    	service.findAll(id, function(data) {
		    	_.forEach(data, function(element) {
		            /**
		             * add this element
		             */
		    		list.push(element);
		        });
				$log.debug('loaded ', name, list);
				if(callback) {
					callback(list);
				}
		    }, toastService.failure);
	    },
	    get : function(id, callback, service) {
	    	service.get(id, function(data) {
	    		callback(data);
		    	$log.debug('[GET/scope] ' + data.name + '#' + data.id);
		    }, toastService.failure);
	    }
  }
return {
	  crud:crud,
	  links:crudLinks,
	  scope: {
		  collections: {
			  new: scopeCrud.new,
			  findAll: scopeCrud.findAll
		  },
		  entity: {
			  get: scopeCrud.get,
			  remove: scopeCrud.remove,
			  save: scopeCrud.save,
			  duplicate: scopeCrud.duplicate
		  },
		  link: {
			  add: scopeCrud.addLink,
			  save: scopeCrud.updateLink,
			  remove: scopeCrud.removeLink
		  },
		  combo: {
			  findAll: scopeCrud.findAllIdName
		  }
	  }
  }
  }])
  .factory('genericPickerService', [ '$log', '$mdDialog', 'toastService', function($log, $mdDialog, toastService) {
	  /**
	   * pickers
	   */
	  var pickers = {
			  confirm : function(title, content, ok, cancel) {
					var confirm = $mdDialog.confirm()
						.title(title)
						.textContent(content)
						.ariaLabel('Confirm')
						.ok(ok)
						.cancel(cancel);
					return $mdDialog.show(confirm);
			  },
			  nodes : function(ev, callback, abort, ctrl) {
			        $mdDialog.show({
			          controller: ctrl,
			          templateUrl: 'js/partials/dialog/nodesDialog.tmpl.html',
			          parent: angular.element(document.body),
			          targetEvent: ev,
			          clickOutsideToClose:true
			        })
			        .then(function(node) {
			        	callback(node);
			        }, function() {
			            abort();
			        });
			  },
			  graph : function(event, anchor, callback, abort, ctrl) {
			        $mdDialog.show({
			          locals: {
			            anchor: anchor
					  },
					  bindToController : true,
			          controller: ctrl,
			          templateUrl: 'js/partials/dialog/graphDialog.tmpl.html',
			          targetEvent: event,
			          clickOutsideToClose:true
			        })
			        .then(function(node) {
			        	callback(node);
			        }, function() {
			            abort();
			        });
			  }
	  }
	  return {
		  pickers : pickers
	  }
  }]);
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

angular.module('JarvisApp.ctrl.home', ['JarvisApp.services'])
.controller('homeCtrl', 
		['$scope', '$store', '$log', 'viewResourceService', 'iotResourceService', 'toastService',
	function($scope, $store, $log, viewResourceService, iotResourceService, toastService){
    /**
     * swipe left
     */
    $scope.onSwipeLeft = function() {
    	if($scope.tabIndex >= ($scope.views.length-1)) {
    		$scope.tabIndex = $scope.views.length-1;
    		return;
    	}
    	$scope.tabIndex++;
    }
    /**
     * swipe right
     */
    $scope.onSwipeRight = function() {
    	if($scope.tabIndex <= 0) {
    		$scope.tabIndex = 0;
    		return;
    	}
    	$scope.tabIndex--;
    }
    /**
     * select tab callback
     */
    $scope.selectTab = function(view, index) {
    	$scope.tabIndex = index;
    }
    /**
     * is selected
     */
    $scope.activeTab = function(view, index) {
    	return $scope.tabIndex == index;
    }
    /**
     * load this controller
     */
    $scope.load = function() {
    	$scope.store = $store;
    	$scope.views = [];
    	$scope.tabIndex = -1;
    	
	    /**
	     * loading views
	     */
		viewResourceService.view.findAll(function(data) {
	        var arr = [];
	    	_.forEach(data, function(element) {
	            /**
	             * load this view only if ishome
	             */
	            if(element.ishome) {
	            	element.urlBackground = "url('"+element.url+"')";
	            	arr.push(element);
	            }
	        });
	    	toastService.info(arr.length + ' view(s)');
	    	
	    	_.forEach(arr, function(view) {
	    		$log.info('loading view', view);
	    		viewResourceService.iots.findAll(view.id, function(data) {
	    			view.iots = data;
	    			var done = _.after(view.iots.length, function() {
	    				$log.debug('Linked iots to view', view.iots);
	    			});
	    			_.forEach(view.iots, function(iot){
	    				/**
	    				 * render each view
	    				 */
	    		      	iotResourceService.iot.task(iot.id, 'render', {}, function(data) {
	    		      		iot.render = data;
	    		      		done(iot);
	    		      	}, toastService.failure);
	    			});
	    	    }, toastService.failure);
			});
	    	
	        $scope.views = arr;
	        if($scope.views.length > 0) {
	        	$scope.tabIndex = 0;
	        }
	    }, toastService.failure);
	
		$log.info('views-ctrl');
    }
}])
.controller('helperCtrl', 
		[ '$scope', '$store', '$log', 'viewResourceService', 'iotResourceService', 'toastService',
	function($scope, $store, $log, viewResourceService, iotResourceService, toastService){
}])
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

angular.module('JarvisApp.services.iot', [])
	.factory('iotResourceService', [ 'genericResourceService', function(genericResourceService) {
		return {
			iot: genericResourceService.crud(['iots']),
			plugins : genericResourceService.links(['iots'], ['plugins','scripts']),
			iots : genericResourceService.links(['iots'], ['iots']),
			triggers : genericResourceService.links(['iots'], ['triggers'])
		}
	}]);
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

angular.module('JarvisApp.ctrl.iots', ['JarvisApp.services'])
.controller('iotsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'iotResourceService',
	function($scope, $log, genericScopeService, iotResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.iots = entities;
			},
			function() {
				return $scope.iots;
			},
			$scope, 
			'iots', 
			iotResourceService.iot,
			{
    			name: "object name",
    			icon: "list"
    		}
	);
}])
.controller('iotCtrl',
		['$scope', '$log', '$state', '$stateParams', 'iotResourceService', 'pluginResourceService', 'genericScopeService', 'genericResourceService', 'toastService',
	function($scope, $log, $state, $stateParams, iotResourceService, pluginResourceService, genericScopeService, genericResourceService, toastService){
	$scope.getLink = function() {
		return $scope.plugins;
	}
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'iot', 
			'iots', 
			iotResourceService.iot
	);
	/**
	 * declare links
	 */
	$scope.links = {
			plugins: {},
			triggers: {},
			iots: {}
	}
	/**
	 * declare action links
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.plugins;
			},
			$scope.links.plugins,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.plugins, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.triggers, 
			{'order':'1'},
			$stateParams.id
	);
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.iots;
			},
			$scope.links.iots,
			'iot',
			'iots',
			iotResourceService.iot, 
			iotResourceService.iots, 
			{'order':'1'},
			$stateParams.id
	);
    /**
	 * render this iot, assume no args by default
	 * @param iot, the iot to render
	 */
	$scope.render = function(iot) {
	 	iotResourceService.iot.task(iot.id, 'render', {}, function(data) {
	 		$log.debug('iotCtrl::render', data);
	 	    $scope.renderdata = data;
	 	    $scope.output = angular.toJson(data, true);
	    }, toastService.failure);
	}
	/**
	 * init this controller
	 */
	$scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
	 
		/**
		 * get current iot
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.iot=update}, iotResourceService.iot);
	
		/**
		 * get all plugins
		 */
    	$scope.plugins = [];
    	genericResourceService.scope.collections.findAll('plugins', $stateParams.id, $scope.plugins, iotResourceService.plugins);
    	$scope.iots = [];
    	genericResourceService.scope.collections.findAll('iots', $stateParams.id, $scope.iots, iotResourceService.iots);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, iotResourceService.triggers);
	
		$log.info('iot-ctrl');
	}
}])
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

/**
 * pluginResourceService
 */
angular.module('JarvisApp.services.plugin', []).factory('pluginResourceService', 
		[ '$log', 'Restangular', 'filterService', 'commandResourceService', 'genericResourceService',
		  function($log, Restangular, filterService, commandResourceService, genericResourceService) {
  var plugins = {
	        /**
			 * base services : findAll, delete, put and post
			 */
			findAll: function(callback, failure) {
                var arr = [];
                var plugins = ['scripts'];
				var done = _.after(plugins.length, function(loaded) {
					callback(loaded);
				});
				_.forEach(plugins, function(plugin) {
					Restangular.all('plugins').all(plugin).getList().then(function(elements) {
		            	_.forEach(elements, function(element) {
		                    arr.push(filterService.plain(element));
		                });
		            	done(arr);
					},function(errors){
						failure(errors);
					});
				});
			}
  };
  return {
	    plugins: plugins,
	    scripts: genericResourceService.crud(['plugins','scripts']),
	    commands : genericResourceService.links(['plugins','scripts'], ['commands'])
  }
}]);
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

angular.module('JarvisApp.ctrl.plugins', ['JarvisApp.services'])
.controller('pluginsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'pluginResourceService',
	function($scope, $log, genericScopeService, pluginResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.plugins = entities;
			},
			function() {
				return $scope.plugins;
			},
			$scope, 
			'plugins/scripts', 
			pluginResourceService.scripts,
			{
    			name: "script name",
    			icon: "list",
       			type: "script"
    		}
	);
}])
.controller('pluginScriptCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'genericPickerService', 'pluginResourceService', 'iotResourceService', 'commandResourceService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, genericPickerService, pluginResourceService, iotResourceService, commandResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'script', 
			'plugins', 
			pluginResourceService.scripts
	);
	/**
	 * declare links
	 */
	$scope.links = {
			commands: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.commands;
			},
			$scope.links.commands, 
			'script', 
			'plugins', 
			pluginResourceService.scripts, 
			pluginResourceService.commands, 
			{
    			'order':'1',
    			'name':'noname',
       			'nature':'info',
       			'type':'json'
			},
			$stateParams.id
	);
    /**
     * execute these command on server side (only action command)
	 * @param command, the command to be executed
     */
    $scope.execute = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
    		pluginResourceService.scripts.task(command.id, 'execute', $scope.rawTestData, function(data) {
       	    	$log.debug('pluginScriptCtrl::execute', command, data);
       	    	$scope.rawoutput = data;
       	    	$scope.output = angular.toJson(data, true);
    	    }, toastService.failure);
    	}
    }
    /**
     * render these command on server side (only data command)
	 * @param command, the command to be executed
     */
    $scope.render = function(command) {
    	if(command != undefined && command.id != undefined && command.id != '') {
    		pluginResourceService.scripts.task(command.id, 'render', $scope.rawTestData, function(data) {
       	    	$log.debug('pluginScriptCtrl::render', command, data);
       	    	$scope.rawoutput = data;
       	    	$scope.output = angular.toJson(data, true);
    	    }, toastService.failure);
    	}
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
    	$scope.rawoutput = {};
    	$scope.output = angular.toJson({}, true);
    	$scope.rawTestData = {};
    	$scope.editTestData = angular.toJson($scope.rawTestData, true);
    	$scope.jsonTestData = angular.toJson($scope.rawTestData, true);
    }
    /**
     * set owner
     */
    $scope.setOwner = function(owner) {
    	$log.debug('setOwner', owner);
    	$scope.script.owner = owner.id;
    }
    /**
     * load controller
     */
    $scope.load = function() {
    	$scope.activeTab = $stateParams.tab;
    	
    	$scope.clear();
    	
	    /**
	     * init part
	     */
		$scope.combo = {
				booleans: [
		               	   {id: true, value:'True'},
		               	   {id: false,value:'False'}
		        ]
		};
	
		/**
		 * get current script
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.script=update}, pluginResourceService.scripts);
	
		/**
		 * get all commands
		 */
    	$scope.commands = [];
    	genericResourceService.scope.collections.findAll('commands', $stateParams.id, $scope.commands, pluginResourceService.commands);

		/**
		 * find all owner
		 */
    	$scope.combo.owners = [{id: undefined, name: "iot.empty"}];
    	genericResourceService.scope.combo.findAll('owner', $scope.combo.owners, iotResourceService.iot);

		$log.info('script-ctrl');
    }
}])
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

angular.module('JarvisApp.ctrl.properties', ['JarvisApp.services'])
.controller('propertiesCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'propertyResourceService',
	function($scope, $log, genericScopeService, propertyResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.properties = entities;
			},
			function() {
				return $scope.properties;
			},
			$scope, 
			'properties', 
			propertyResourceService.property,
			{
    			key: "key",
    			value: "value"
    		}
	);
}])
.controller('propertyCtrl',
		[ '$scope', '$log', '$stateParams', '$mdDialog', 'genericResourceService', 'genericScopeService', 'propertyResourceService', 'toastService',
	function($scope, $log, $stateParams, $mdDialog, genericResourceService, genericScopeService, propertyResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'property', 
			'properties', 
			propertyResourceService.property);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current property
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.property=update;
    	}, propertyResourceService.property);

    	$log.info('property-ctrl', $scope.properties);
    }
}])
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

/**
 * viewResourceService
 */
angular.module('JarvisApp.services.property', []).factory('propertyResourceService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  property : genericResourceService.crud(['properties'])
  }
}]);
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

/* Controllers */

angular.module('JarvisApp.routes',['JarvisApp.config'])
    .config( ['$urlRouterProvider', function($urlRouterProvider) {
		var $log =  angular.injector(['ng']).get('$log');
		$log.info('$urlRouterProvider', $urlRouterProvider);
        /**
         * default state
         */
        $urlRouterProvider.otherwise('/home');
    }])
    .config(['$stateProvider', function($stateProvider) {
		var $log =  angular.injector(['ng']).get('$log');
		$log.info('$stateProvider', $stateProvider);
        /**
         * now set up the state
         */
        $stateProvider
        .state('home', {
            url: '/home',
            controller: 'homeCtrl',
            templateUrl: '/ui/js/partials/home/page.html'
        })
        .state('helper-iots', {
            url: '/helper-iots',
            controller: 'helperCtrl',
            templateUrl: '/ui/js/partials/helper/jarvis-commands.svg'
        })
        .state('helper-scenarii', {
            url: '/helper-scenarii',
            controller: 'helperCtrl',
            templateUrl: '/ui/js/partials/helper/jarvis-scenarii.svg'
        })
        .state('events', {
            url: '/events',
            controller: 'eventsCtrl',
            templateUrl: '/ui/js/partials/events/page.html'
        })
        .state('triggers', {
            url: '/triggers',
            controller: 'triggersCtrl',
            templateUrl: '/ui/js/partials/triggers/page.html'
        })
        .state('triggers-by-id', {
            url: '/triggers/:id?tab',
            controller: 'triggerCtrl',
            templateUrl: '/ui/js/partials/triggers/trigger/page.html'
        })
        .state('iots', {
            url: '/iots',
            controller: 'iotsCtrl',
            templateUrl: '/ui/js/partials/iots/page.html'
        })
        .state('iots-by-id', {
            url: '/iots/:id?tab',
            controller: 'iotCtrl',
            templateUrl: '/ui/js/partials/iots/iot/page.html'
        })
        .state('plugins', {
            url: '/plugins',
            controller: 'pluginsCtrl',
            templateUrl: '/ui/js/partials/plugins/page.html'
        })
        .state('plugins-by-id-script', {
            url: '/plugins/scripts/:id?tab',
            controller: 'pluginScriptCtrl',
            templateUrl: '/ui/js/partials/plugins/script/page.html'
        })
        .state('commands', {
            url: '/commands',
            controller: 'commandsCtrl',
            templateUrl: '/ui/js/partials/commands/page.html'
        })
        .state('commands-by-id', {
            url: '/commands/:id',
            controller: 'commandCtrl',
            templateUrl: '/ui/js/partials/commands/command/page.html'
        })
        .state('views', {
            url: '/views',
            controller: 'viewsCtrl',
            templateUrl: '/ui/js/partials/views/page.html'
        })
        .state('views-by-id', {
            url: '/views/:id',
            controller: 'viewCtrl',
            templateUrl: '/ui/js/partials/views/view/page.html'
        })
        .state('configurations', {
            url: '/configurations',
            controller: 'configurationsCtrl',
            templateUrl: '/ui/js/partials/configurations/page.html'
        })
        .state('configurations-by-id', {
            url: '/configurations/:id',
            controller: 'configurationCtrl',
            templateUrl: '/ui/js/partials/configurations/configuration/page.html'
        })
        .state('properties', {
            url: '/properties',
            controller: 'propertiesCtrl',
            templateUrl: '/ui/js/partials/properties/page.html'
        })
        .state('properties-by-id', {
            url: '/properties/:id',
            controller: 'propertyCtrl',
            templateUrl: '/ui/js/partials/properties/property/page.html'
        })
        .state('connectors', {
            url: '/connectors',
            controller: 'connectorsCtrl',
            templateUrl: '/ui/js/partials/connectors/page.html'
        })
        .state('connectors-by-id', {
            url: '/connectors/:id',
            controller: 'connectorCtrl',
            templateUrl: '/ui/js/partials/connectors/connector/page.html'
        })
        .state('snapshots', {
            url: '/snapshots',
            controller: 'snapshotsCtrl',
            templateUrl: '/ui/js/partials/snapshots/page.html'
        })
        .state('snapshots-by-id', {
            url: '/snapshots/:id',
            controller: 'snapshotCtrl',
            templateUrl: '/ui/js/partials/snapshots/snapshot/page.html'
        })
        .state('crons', {
            url: '/crons',
            controller: 'cronsCtrl',
            templateUrl: '/ui/js/partials/crons/page.html'
        })
        .state('crons-by-id', {
            url: '/crons/:id',
            controller: 'cronCtrl',
            templateUrl: '/ui/js/partials/crons/cron/page.html'
        })
        .state('scenarios', {
            url: '/scenarios',
            controller: 'scenariosCtrl',
            templateUrl: '/ui/js/partials/scenarios/page.html'
        })
        .state('scenarios-by-id', {
            url: '/scenarios/:id?tab',
            controller: 'scenarioCtrl',
            templateUrl: '/ui/js/partials/scenarios/scenario/page.html'
        })
        .state('blocks', {
            url: '/blocks',
            controller: 'blocksCtrl',
            templateUrl: '/ui/js/partials/blocks/page.html'
        })
        .state('blocks-by-id', {
            url: '/blocks/:id?tab',
            controller: 'blockCtrl',
            templateUrl: '/ui/js/partials/blocks/block/page.html'
        })
        ;
    }])
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

/**
 * scenarioResourceService
 */
angular.module('JarvisApp.services.scenario', []).factory('scenarioResourceService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  scenario: genericResourceService.crud(['scenarios']),
	  blocks  : genericResourceService.links(['scenarios'], ['blocks']),
	  triggers: genericResourceService.links(['scenarios'], ['triggers'])
  }
}]);
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

angular.module('JarvisApp.ctrl.scenarios', ['JarvisApp.services'])
.controller('scenariosCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'scenarioResourceService', 'toastService',
	function($scope, $log, genericScopeService, scenarioResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.scenarios = entities;
			},
			function() {
				return $scope.scenarios;
			},
			$scope, 
			'scenarios', 
			scenarioResourceService.scenario,
			{
    			name: "scenario name",
    			icon: "list"
    		}
	);
}])
.controller('scenarioCtrl',
		[ '$scope',
		  '$log',
		  '$stateParams',
		  '$mdDialog',
		  'genericScopeService',
		  'genericResourceService',
		  'genericPickerService',
		  'scenarioResourceService',
		  'blockResourceService',
		  'pluginResourceService',
		  'toastService',
	function(
			$scope,
			$log,
			$stateParams,
			$mdDialog,
			genericScopeService,
			genericResourceService,
			genericPickerService,
			scenarioResourceService,
			blockResourceService,
			pluginResourceService,
			toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario);
	/**
	 * declare links
	 */
	$scope.links = {
			blocks: {},
			triggers: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.blocks;
			},
			$scope.links.blocks,
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario,
			scenarioResourceService.blocks, 
			{
				'order':'1'
			},
			$stateParams.id
	);
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.triggers;
			},
			$scope.links.triggers,
			'scenario', 
			'scenarios', 
			scenarioResourceService.scenario,
			scenarioResourceService.triggers, 
			{
				'order':'1'
			},
			$stateParams.id
	);
    /**
     * render scenario
     */
    $scope.build = function(resource) {
    	var codes = [];
    	_.each(resource, function(graph) {
    		$log.debug("graph", graph.stage);
        	var code = "";
	    	_.each(graph.nodes, function(node) {
	    		if(node.start) {
	    			code += '#' + node.id + '=>start: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.end) {
	    			code += '#' + node.id + '=>end: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.activity) {
	    			code += '#' + node.id + '=>operation: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.gateway) {
	    			code += '#' + node.id + '=>condition: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    		if(node.call) {
	    			code += '#' + node.id + '=>subroutine: ' + node.name + ':>'+node.longId+'\n';
	    		}
	    	});
	    	_.each(graph.edges, function(edge) {
	    		if(graph.nodes[edge.sourceId].gateway) {
	        		if(edge.bool) {
	        			code += '#' + edge.sourceId + '(yes, right)->#' + edge.targetId + '\n';
	        		} else {
	        			code += '#' + edge.sourceId + '(no)->#' + edge.targetId + '\n';
	        		}
	    		} else {
	    			code += '#' + edge.sourceId + '->#' + edge.targetId + '\n';
	    		}
	    	});
        	codes.push({"stage":graph.stage, "code": code});
    	});
    	return codes;
    }
    /**
     * render each graph
     */
    $scope.render = function(resources) {
		$log.debug(resources);
    	$('#canvas').html('');
    	_.each(resources, function(result) {
        	$('#canvas').append('<h4>'+result.stage+'</h4>');
        	$('#canvas').append('<div id="canvas-'+result.stage+'"></div>');
    	});
    	_.each(resources, function(result) {
    		$log.info("Render", result.stage);
    		var stage = result.stage;
 	    	var chart = flowchart.parse(result.code);
	        chart.drawSVG('canvas-'+result.stage, {
	          // 'x': 30,
	          // 'y': 50,
	          'line-width': 3,
	          'line-length': 50,
	          'text-margin': 10,
	          'font-size': 12,
	          'font': 'normal',
	          'font-family': 'Helvetica',
	          'font-weight': 'normal',
	          'font-color': 'black',
	          'line-color': 'black',
	          'element-color': 'black',
	          'fill': 'white',
	          'yes-text': 'yes',
	          'no-text': 'no',
	          'arrow-end': 'block',
	          'scale': 1,
	          'symbols': {
	            'start': {
	              'font-color': 'green',
	              'element-color': 'green',
	              'fill': 'yellow'
	            },
	            'end':{
	              'font-color': 'red',
	              'element-color': 'red',
	              'fill': 'yellow'
	            }
	          },
	          'flowstate' : {
	            'past' : { 'fill' : '#CCCCCC', 'font-size' : 12},
	            'current' : {'fill' : 'yellow', 'font-color' : 'red', 'font-weight' : 'bold'},
	            'future' : { 'fill' : '#FFFF99'},
	            'request' : { 'fill' : 'blue'},
	            'invalid': {'fill' : '#444444'},
	            'approved' : { 'fill' : '#58C4A3', 'font-size' : 12, 'yes-text' : 'APPROVED', 'no-text' : 'n/a' },
	            'rejected' : { 'fill' : '#C45879', 'font-size' : 12, 'yes-text' : 'n/a', 'no-text' : 'REJECTED' }
	          }
	        });
    	});
    }
    /**
     * load this controller
     */
    $scope.chart = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		scenarioResourceService.scenario.task(scenario.id, 'render', {}, function(data) {
       	    	$log.debug('[SCENARIO/render]', scenario, data);
       	    	$scope.codes = $scope.build(data);
       	    	$scope.render($scope.codes);
    	    }, toastService.failure);
    	}
    }
    /**
     * execute this scenario
	 * @param scenario, the scenario to be executed
     */
    $scope.execute = function(scenario) {
    	if(scenario != undefined && scenario.id != undefined && scenario.id != '') {
    		scenarioResourceService.scenario.task(scenario.id, 'execute', {}, function(data) {
       	    	$log.debug('[SCENARIO/execute]', scenario, data);
       	    	$scope.console = data;
    	    }, toastService.failure);
    	}
    }
    /**
     * load this controller
     */
    $scope.load = function() {
  		$scope.activeTab = $stateParams.tab;
  		$scope.plugin = {};

		/**
		 * get current scenario
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {
    		$scope.scenario=update;
    		$scope.chart(update);
    	}, scenarioResourceService.scenario);

		/**
		 * find all blocks
		 */
    	$scope.blocks = [];
    	genericResourceService.scope.collections.findAll(
    			'blocks', $stateParams.id, $scope.blocks, scenarioResourceService.blocks,
    			function(blocks) {
    				_.forEach(blocks, function(block) {
    					block.plugins = {};
    					block.plugins.if   = [];
    					block.plugins.then = [];
    					block.plugins.else = [];
    					blockResourceService.plugins.if.findAll(block.id, function(data){
    						block.plugins.if = data;
    					});
    					blockResourceService.plugins.then.findAll(block.id, function(data){
    						block.plugins.then = data;
    					});
    					blockResourceService.plugins.else.findAll(block.id, function(data){
    						block.plugins.else = data;
    					});
    					block.blocks = {};
    					block.blocks.then = [];
    					block.blocks.else = [];
    					blockResourceService.blocks.then.findAll(block.id, function(data){
    						block.blocks.then = data;
    					});
    					blockResourceService.blocks.else.findAll(block.id, function(data){
    						block.blocks.else = data;
    					});
    					$log.debug("blocks.plugins", block.plugins);
    					$log.debug("blocks.blocks", block.blocks);
    				});
    			}
    	);
    	$scope.triggers = [];
    	genericResourceService.scope.collections.findAll('triggers', $stateParams.id, $scope.triggers, scenarioResourceService.triggers);

		$log.debug('scenario-ctrl', $scope.scenario);
    }
}])
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

/**
 * genericScopeService
 */
angular.module('JarvisApp.services.scope', ['JarvisApp.services.generic']).factory('genericScopeService', 
		[ '$log', '$filter', 'genericResourceService', 'genericPickerService', 'toastService', function($log, $filter, genericResourceService, genericPickerService, toastService) {
  var scope = {
	  resources : function(setEntities, getEntities, scope, resource, service, init) {
	    	$log.debug("inject default resources scope", resource);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.create = function(create, callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		create,
		        		service,
		        		callback
		        );
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.new = function(callback) {
		    	genericResourceService.scope.collections.new(
		    			resource,
		        		getEntities(),
		        		init,
		        		service,
		        		callback
		        );
		    }
			scope.crud = genericResourceService.crud(resource.split('/'));
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(entity,callback) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:entity.name, id:entity.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
			    	$log.debug('remove', entity);
					var toremove = entity.id;
					_.remove(getEntities(), function(element) {
						return element.id == toremove;
					});
			    	scope.crud.delete(entity.id);
			    	if(callback) {
			    		callback(entity);
			    	}
		    	}, function() {
			    	$log.debug('abort');
		    	});
		    }
		    /**
		     * loading
		     */
		    scope.load = function(callback) {
		    	setEntities([]);
		    	service.findAll(function(data) {
		    		setEntities(data);
					$log.debug(resource+'-ctrl', data);
					if(callback) {
						callback();
					}
			    }, toastService.failure);
		    }
	  },
	  resource : function(scope, resource, back, service, link, dataLink, stateParamsId) {
	    	$log.debug("inject default resource scope", resource);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.drop', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
		    			genericResourceService.scope.entity.remove(function() {scope.go(back)}, resource, data, service);
			    	}, function() {
				    	$log.debug('abort');
			   	});
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.save = function(data) {
		    	genericResourceService.scope.entity.save(resource, data, service);
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.duplicate = function(data) {
		    	genericResourceService.scope.entity.duplicate(function() {scope.go(back)}, resource, data, service);
		    }
	  },
	  resourceLink : function(getLink, scope, resource, back, service, link, dataLink, stateParamsId, callbacks) {
	    	$log.debug('inject link',link);
		    /**
		     * Cf. genericResourceService
		     */
		    scope.add = function(data) {
		    	$log.debug('link::add',data);
		    	genericResourceService.scope.link.add(stateParamsId,data,dataLink,link,getLink());
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.update = function(data) {
		    	$log.debug('link::update',data);
		    	genericResourceService.scope.link.save(stateParamsId,data,link);
			}
		    /**
		     * Cf. genericResourceService
		     */
		    scope.drop = function(data) {
		    	genericPickerService.pickers.confirm(
		    			$filter('translate')('action.remove.link', {name:data.name, id:data.id}),
		    			'',
		    			$filter('translate')('yes'),
		    			$filter('translate')('no')
		    		)
		    		.then(function() {
				    	$log.debug('link::drop',data);
				    	if(callbacks && callbacks.remove) {
				    		/**
				    		 * alternate remove function
				    		 */
				    		callbacks.remove(data);
				    	} else {
				    		/**
				    		 * default remove function
				    		 */
					    	genericResourceService.scope.link.remove(stateParamsId,data,link,getLink());
				    	}
				    	/**
				    	 * post drop callback
				    	 */
				    	if(callbacks && callbacks.afterRemove) {
				    		callbacks.afterRemove(data);
				    	}
			    	}, function() {
				    	$log.debug('abort');
			    	}
			    );
			}
	  }
  };
  return {
	  scope:scope
  }
}]);
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
 * labelResourceService
 */
myAppServices.factory('labelResourceService', [ '$q', '$window', '$rootScope', 'Restangular', function($q, $window, $rootScope, Restangular) {
  var $log =  angular.injector(['ng']).get('$log');
  $log.info('labelResourceService', $q);
  return {
        findAll: function(callback,failure) {
        	// Restangular returns promises
        	Restangular.all('labels').getList().then(function(labels) {
        	  callback(labels);
        	},function(errors){
        		failure(errors);
        	});
        }
  }
}]);

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

/**
 * snapshotResourceService
 */
angular.module('JarvisApp.services.snapshot', []).factory('snapshotResourceService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  snapshot : genericResourceService.crud(['snapshots'])
  }
}]);
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

angular.module('JarvisApp.ctrl.snapshots', ['JarvisApp.services'])
.controller('snapshotsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'snapshotResourceService',
	function($scope, $log, genericScopeService, snapshotResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.snapshots = entities;
			},
			function() {
				return $scope.snapshots;
			},
			$scope, 
			'snapshots', 
			snapshotResourceService.snapshot,
			{
    			name: "snapshot name",
    			icon: "list"
    		}
	);
}])
.controller('snapshotCtrl',
		['$scope', '$log', '$stateParams', '$filter', '$http', 'genericResourceService', 'genericScopeService', 'snapshotResourceService', 'iotResourceService', 'toastService',
	function($scope, $log, $stateParams, $filter, $http, genericResourceService, genericScopeService, snapshotResourceService, iotResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'snapshot', 
			'snapshots', 
			snapshotResourceService.snapshot);
    /**
     * restore configuration with this snapshot
	 * @param snapshot, the snapshot
     */
    $scope.restore = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		snapshotResourceService.snapshot.task(snapshot.id, 'restore', {}, function(data) {
       	    	$log.debug('snapshotCtrl::restore', snapshot, data);
    	    }, toastService.failure);
    	}
    }
    /**
     * download current snapshot
	 * @param snapshot, the snapshot to be downloaded
     */
    $scope.download = function(snapshot) {
    	if(snapshot != undefined && snapshot.id != undefined && snapshot.id != '') {
    		snapshotResourceService.snapshot.task(snapshot.id, 'download', {}, function(data) {
    			var fileName = 'export-'+$filter('date')(new Date(), 'yyyyMMdd-HHmmss') + '.json';
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style = "display: none";
       	    	$log.debug('snapshot', snapshot, data);
       	    	var file = new Blob([angular.toJson(data, true)], {type: 'application/text'});
                var fileURL = window.URL.createObjectURL(file);
                a.href = fileURL;
                a.download = fileName;
                a.click();
    	    }, toastService.failure);
    	}
    }
    /**
     * load local file
	 * @param id of input file
     */
    $scope.upload = function(id) {
    	$('#'+id).trigger('click');
    }
    /**
     * upload callback
	 * @param snapshot, the snapshot
	 * @param file, data to store in snapshot (on client side)
     */
    $scope.loaded = function(snapshot, file) {
    	snapshot.json = file.data;
    	$log.debug('loaded', snapshot, file);
    }
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current snapshot
		 */
    	$scope.snapshots = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.snapshot=update}, snapshotResourceService.snapshot);

		$log.info('snapshot-ctrl', $scope.snapshots);
    }
}])
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

/* Cf. https://angular-md-color.com */

angular.module('JarvisApp.theme', [ 'ngMaterial' ]).config([ '$mdThemingProvider', function ($mdThemingProvider) {
    var customPrimary = {
            '50': '#aaeafd',
            '100': '#92e4fc',
            '200': '#79defc',
            '300': '#60d8fb',
            '400': '#47d2fb',
            '500': '#2ECCFA',
            '600': '#15c6f9',
            '700': '#06bbef',
            '800': '#05a7d6',
            '900': '#0594bd',
            'A100': '#c3f0fe',
            'A200': '#dcf7fe',
            'A400': '#f5fdff',
            'A700': '#0480a5'
        };
        $mdThemingProvider
            .definePalette('customPrimary', 
                            customPrimary);

        var customAccent = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#f8fcfe',
            '400': '#e3f4f9',
            '500': '#CEECF5',
            '600': '#b9e4f1',
            '700': '#a4dcec',
            '800': '#8ed3e8',
            '900': '#79cbe4',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#64c3df'
        };
        $mdThemingProvider
            .definePalette('customAccent', 
                            customAccent);

        var customWarn = {
            '50': '#ff8080',
            '100': '#ff6666',
            '200': '#ff4d4d',
            '300': '#ff3333',
            '400': '#ff1a1a',
            '500': '#FF0000',
            '600': '#e60000',
            '700': '#cc0000',
            '800': '#b30000',
            '900': '#990000',
            'A100': '#ff9999',
            'A200': '#ffb3b3',
            'A400': '#ffcccc',
            'A700': '#800000'
        };
        $mdThemingProvider
            .definePalette('customWarn', 
                            customWarn);

        var customBackground = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#ffffff',
            '400': '#ffffff',
            '500': '#F2F2F2',
            '600': '#e5e5e5',
            '700': '#d8d8d8',
            '800': '#cccccc',
            '900': '#bfbfbf',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#b2b2b2'
        };
        $mdThemingProvider
            .definePalette('customBackground', 
                            customBackground);

       $mdThemingProvider.theme('default')
           .primaryPalette('customPrimary')
           .accentPalette('customAccent')
           .warnPalette('customWarn')
           .backgroundPalette('customBackground')
    }]);/* 
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

angular.module('JarvisApp.services.trigger', [])
	.factory('triggerResourceService', [ 'genericResourceService', function(genericResourceService) {
		return {
			trigger: genericResourceService.crud(['triggers'])
		}
	}]);
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

angular.module('JarvisApp.ctrl.triggers', ['JarvisApp.services'])
.controller('triggersCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'triggerResourceService',
		function($scope, $log, genericScopeService, triggerResourceService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.triggers = entities;
			},
			function() {
				return $scope.triggers;
			},
			$scope, 
			'triggers', 
			triggerResourceService.trigger,
			{
    			name: "trigger name",
    			icon: "settings_remote"
    		}
	);
}])
.controller('triggerCtrl',
		[ '$scope', '$log', '$stateParams', 'genericResourceService', 'genericScopeService', 'triggerResourceService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, triggerResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'trigger', 
			'triggers', 
			triggerResourceService.trigger
	);
    /**
     * loading
     */
    $scope.load = function() {
		/**
		 * get current trigger
		 */
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.trigger=update}, triggerResourceService.trigger);
	
		$log.info('trigger-ctrl');
    }
}])
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

/**
 * viewResourceService
 */
angular.module('JarvisApp.services.view', []).factory('viewResourceService', [ 'genericResourceService', function(genericResourceService) {
  return {
	  view : genericResourceService.crud(['views']),
	  iots : genericResourceService.links(['views'], ['iots'])
  }
}]);
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

angular.module('JarvisApp.ctrl.views', ['JarvisApp.services'])
.controller('viewsCtrl', 
		[ '$scope', '$log', 'genericScopeService', 'viewResourceService',
	function($scope, $log, genericScopeService, viewResourceService){
    $log.info('viewsCtrl');
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resources(
			function(entities) {
				$scope.views = entities;
			},
			function() {
				return $scope.views;
			},
			$scope, 
			'views', 
			viewResourceService.view,
			{
    			name: "view name",
    			icon: "list"
    		}
	);
}])
.controller('viewCtrl',
		[ '$scope, $log', '$stateParams', 'genericResourceService', 'genericScopeService', 'viewResourceService', 'iotResourceService', 'toastService',
	function($scope, $log, $stateParams, genericResourceService, genericScopeService, viewResourceService, iotResourceService, toastService){
	/**
	 * declare generic scope resource (and inject it in scope)
	 */
	genericScopeService.scope.resource(
			$scope, 
			'view', 
			'views', 
			viewResourceService.view);
	/**
	 * declare links
	 */
	$scope.links = {
			iots: {}
	};
	/**
	 * declare generic scope resource link (and inject it in scope)
	 */
	genericScopeService.scope.resourceLink(
			function() {
				return $scope.iots;
			},
			$scope.links.iots, 
			'view', 
			'views', 
			viewResourceService.view, 
			viewResourceService.iots, 
			{
    			'order':'1'
			},
			$stateParams.id
	);
    /**
     * load this controller
     */
    $scope.load = function() {
		/**
		 * get current view
		 */
    	$scope.views = [];
    	genericResourceService.scope.entity.get($stateParams.id, function(update) {$scope.view=update}, viewResourceService.view);
	
		/**
		 * get all views
		 */
		$scope.iots = [];
    	genericResourceService.scope.collections.findAll('iots', $stateParams.id, $scope.iots, viewResourceService.iots);

		$log.info('view-ctrl', $scope.views);
    }
}])
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

/* Controllers */

angular.module('JarvisApp.websocket',['angular-websocket'])
    // WebSocket works as well
    .factory('$store', [ '$log', '$websocket', '$window', function($log, $websocket, $window) {
      $log.info("Websocket: ", $window.location);
      // Open a WebSocket connection
      var dataStream = $websocket('ws://'+$window.location.hostname+':'+$window.location.port+'/stream/');

      var upsert = function (arr, key, newval) {
    	    var match = _.find(arr, key);
    	    if(match){
    	        var index = _.indexOf(arr, _.find(arr, key));
    	        arr[key] = newval;
    	    } else {
    	        arr.push(newval);
    	    }
      };

      var collection = {};
      dataStream.onMessage(function(message) {
    	var entity = JSON.parse(message.data);
    	if(collection[entity.classname] === undefined) {
    		collection[entity.classname] = {};
    	}
        collection[entity.classname][entity.instance] = entity.data;
      });

      var methods = {
        collection: collection,
        get: function() {
          dataStream.send(JSON.stringify({ action: 'get' }));
        }
      };

      return methods;
    }]);
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
