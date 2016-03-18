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
    .config(function($mdIconProvider) {
	})
	.config(['$translateProvider', 
	    function($translateProvider){
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
    .config(['RestangularProvider',
        function(RestangularProvider) {
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
    .config(function($mdThemingProvider){
        // Configure a dark theme with primary foreground yellow
        $mdThemingProvider.theme('docs-dark','default')
            .primaryPalette('deep-orange')
            .warnPalette('red')
            .accentPalette('brown')
            .backgroundPalette('orange')
    })
    /**
     * main controller
     */
    .controller('JarvisAppCtrl',
    	function($scope, $log, $store, $mdDialog, $mdSidenav, $mdMedia, $location, $state, genericPickerService, toastService, iotResourceService, eventResourceService, configurationResourceService){
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
         * load settings
         */
        $scope.saveSettings = function() {
            configurationResourceService.configuration.put($scope.config, function(data) {
            	$log.info("Updated", data);
    	    }, toastService.failure);
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
        	}).then(function(answer) {
        			$log.debug("ok:",answer);
        	   }, function() {
        		   $log.debug("ko:",answer);
        	   }
        	);
        }

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
    })
	.controller('graphDialogCtrl',
		function($scope, $log, $mdDialog, genericResourceService, toastService, anchor) {
		$scope.hide = function() {
		   $mdDialog.hide();
		 };
		$scope.cancel = function() {
		  $mdDialog.cancel();
		};
		$scope.answer = function(answer) {
		  $mdDialog.hide(answer);
		};
	})
	.controller('pickPluginDialogCtrl',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
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
	})
	.controller('pickIotDialogCtrl',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
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
	})
	.controller('pickCommandDialogCtrl',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
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
	})
	.controller('pickTriggerDialogCtrl',
		function($scope, $log, $mdDialog, genericResourceService, toastService) {
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
	})
	.controller('pickBlockDialogCtrl',
			function($scope, $log, $mdDialog, genericResourceService, toastService) {
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
	})
