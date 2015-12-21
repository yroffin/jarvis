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

angular.module('JarvisApp',[
                            'ngMaterial',
                            'ngMdIcons',
                            'restangular',
                            'ui.router',
                            'ui.router.router',
                            'JarvisApp.services'
                            ])
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
    	    	  var newResponse = response;
    	    	  newResponse.originalElement = response;
    	    	  return newResponse
    	    });
    }])
    .config(function($urlRouterProvider) {
        /**
         * default state
         */
        $urlRouterProvider.otherwise('/jobs');
    })
    .config(function($stateProvider) {
        /**
         * now set up the state
         */
        $stateProvider
        .state('jobs', {
            url: '/jobs',
            controller: 'jarvisJobsCtrl',
            templateUrl: '/ui/js/partials/jobs.html'
        })
        .state('jobs-id', {
            url: '/jobs/:id',
            controller: 'jarvisJobCtrl',
            templateUrl: '/ui/js/partials/jobs/job.html'
        })
        .state('configuration', {
            url: '/configuration',
            controller: 'JarvisAppConfCtrl',
            templateUrl: '/ui/js/partials/configuration.html'
        })
        .state('scenario', {
            url: '/scenario',
            controller: 'JarvisAppScenarioCtrl',
            templateUrl: '/ui/js/partials/scenario.html'
        })
        .state('connectors', {
            url: '/connectors',
            controller: 'JarvisAppConnectorsCtrl',
            templateUrl: '/ui/js/partials/connectors.html'
        })
        ;
    })
    .config(function($mdThemingProvider){
        // Configure a dark theme with primary foreground yellow
        $mdThemingProvider.theme('default')
            .primaryPalette('blue')
            .dark();
    })
    /**
     * main controller
     */
    .controller('JarvisAppCtrl',
    ['$scope', '$mdSidenav', '$location', '$state', function($scope, $mdSidenav, $location, $state){
        /**
         * initialize jarvis configuration
         */
        $scope.inventoryItem = {
            href : ''
        };
        $scope.jarvis = {};
        $scope.jarvis.newjob = {};
        $scope.jarvis.configuration = {};
        $scope.jarvis.neo4jdb = {};

        $scope.toastPosition = {
            bottom: false,
            top: true,
            left: false,
            right: true
        };
        $scope.getToastPosition = function() {
            return Object.keys($scope.toastPosition)
                .filter(function(pos) { return $scope.toastPosition[pos]; })
                .join(' ');
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
    }
    ]
    )
    /**
     * configuration
     */
    .controller('JarvisAppLabelsDetailCtrl',
    ['$scope', 'jarvisServices', 'jarvisConfigurationResource', '$mdToast', '$mdDialog',
        function($scope, jarvisServices, jarvisConfigurationResource, $mdToast, $mdDialog) {
            /**
             * load detail on labels
             */
            $scope.label = function(name, ev, $scope, $mdDialog, callback) {
                jarvisConfigurationResource.label({id:name, limit:100}, {id:name, limit:100}, function(data) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content("Labels "+name+" loaded")
                            .position($scope.getToastPosition())
                            .hideDelay(3000)
                    );
                    callback(ev, $scope, $mdDialog, data);
                }, function(failure) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content(failure)
                            .position($scope.getToastPosition())
                            .hideDelay(3000).theme("failure-toast")
                    );
                });
            }

            $scope.showAdvanced = function(ev,item) {
                $scope.label(item.name, ev, $scope, $mdDialog, function(ev, $scope, $mdDialog, data) {
                    $mdDialog.show({
                        controller: function($scope, $mdDialog) {
                            $scope.hide = function() {
                                $mdDialog.hide();
                            };
                            $scope.cancel = function() {
                                $mdDialog.cancel();
                            };
                            $scope.answer = function(answer) {
                                $mdDialog.hide(answer);
                            };
                            $scope.item = item;
                            $scope.data = data;
                        },
                        templateUrl: 'js/partials/dialog/labels.tmpl.html',
                        parent: angular.element(document.body),
                        targetEvent: ev,
                    })
                    .then(function(answer) {
                        $scope.alert = 'You said the information was "' + answer + '".';
                    }, function() {
                        $scope.alert = 'You cancelled the dialog.';
                    });
                });
            }
        }
    ])
    /**
     * configuration
     */
    .controller('JarvisAppConfigCtrl',
    ['$scope', 'jarvisServices', 'jarvisConfigurationResource', '$mdToast', '$mdDialog',
        function($scope, jarvisServices, jarvisConfigurationResource, $mdToast, $mdDialog) {
            $scope.jarvis.configuration.crontabs = undefined;
            $scope.jarvis.configuration.labels = undefined;

            /**
             * load crontabs
             */
            $scope.crontabs = function() {
                jarvisConfigurationResource.crontabs({}, function(data) {
                    $scope.jarvis.configuration.crontabs = data;
                    $mdToast.show(
                        $mdToast.simple()
                            .content("Crontabs loaded")
                            .position($scope.getToastPosition())
                            .hideDelay(3000)
                    );
                }, function(failure) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content(failure)
                            .position($scope.getToastPosition())
                            .hideDelay(3000).theme("failure-toast")
                    );
                });
            }

            /**
             * load crontabs
             */
            $scope.labels = function() {
                jarvisConfigurationResource.labels({}, function(data) {
                    $scope.jarvis.configuration.labels = data;
                    $mdToast.show(
                        $mdToast.simple()
                            .content("Labels loaded")
                            .position($scope.getToastPosition())
                            .hideDelay(3000)
                    );
                }, function(failure) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content(failure)
                            .position($scope.getToastPosition())
                            .hideDelay(3000).theme("failure-toast")
                    );
                });
            }

            $scope.crontabs();
            $scope.labels();
        }
    ])
    /**
     * job view controller
     */
    .controller('jarvisJobsCtrl',
    ['$scope', 'jarvisServices', 'jobResourceService', 'jarvisJobsResource', 'toastService', '$log', '$mdToast',
        function($scope, jarvisServices, jobResourceService, jarvisJobsResource, toastService, $log, $mdToast) {
            $scope.iconPath = 'https://cdn4.iconfinder.com/data/icons/SOPHISTIQUE/web_design/png/128/our_process_2.png';
            /**
             * loading jobs
             */
            jobResourceService.findAll(function(data) {
                var arr = [];
            	_.forEach(data, function(element) {
                    /**
                     * convert internal json params
                     */
                    if(element.params) {
                    	element.text = JSON.stringify(element.params);
                    }
                    arr.push({
                    	'id':element.id,
                    	'name':element.name,
                    	'cronTime':element.cronTime,
                    	'plugin':element.plugin,
                    });
                });
            	toastService.info(arr.length + ' job(s)');
                $scope.jarvis.configuration.jobs = arr;
            }, toastService.failure);

            /**
             * delete this job
             * @param jobs, all jobs for update ui part after sucess deletion
             * @param job
             */
            $scope.delete = function(jobs, job) {
                /**
                 * remove one job
                 */
                jobResourceService.delete(job.id, function(data) {
                	toastService.info('job ' + data.name + '#' + data.id +' deleted');
                	
                    var search = -1;
                    var index = 0;
                	_.forEach(jobs, function(element) {
                        if(element.id === job.id) {
                            search = index;
                        }
                        index++;
                    });
                    if(search >= 0) {
                        delete jobs[search];
                        jobs.splice(search, 1);
                    }

                }, toastService.failure);
            };

            /**
             * update this job
             * @param job
             */
            $scope.save = function(job) {
                var update = {
                    id : job.id,
                    name : job.name,
                    plugin : job.plugin
                };
                /**
                 * create or update this job
                 */
                jobResourceService.put(update, function(data) {
                        job.started = data.started;
                        toastService.info('job ' + data.name + '#' + data.id +' updated');
                    }, toastService.failure);
            }

            /**
             * create a new job
             * @param job
             */
            $scope.new = function(jobs) {
                var update = {
                    name : "Job name"
                };
                /**
                 * create or update this job
                 */
                jobResourceService.post(update, function(data) {
                        toastService.info('job ' + data.name + '#' + data.id +' created');
                        jobs.push(data);
                    }, toastService.failure);
            }

            /**
             * update this job
             * @param job
             */
            $scope.putParam = function(param) {
                /**
                 * add params
                 */
                jobResourceService.param.put(param, function(data) {
                        job.started = data.started;
                        toastService.info('parameter ' + data.name + '#' + data.id +' added');
                    }, toastService.failure);
            }

            /**
             * set started field
             * @param job
             * @param status
             */
            $scope.status = function(job, started) {
                job.started = started;
            };

            /**
             * test plugin associated to this job
             */
            $scope.execute = function(job) {
                $mdToast.show(
                    $mdToast.simple()
                        .content('Test du plugin du job '+job.name)
                        .position($scope.getToastPosition())
                        .hideDelay(3000)
                );
                /**
                 * loading clients
                 */
                jarvisJobsResource.execute(
                    {
                        id: job.id,
                        method: 'test'
                    },
                    {
                        id: job.id,
                        method: 'test'
                    },
                    function(data) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Plugin du job '+job.name+' executé')
                                .position($scope.getToastPosition())
                                .hideDelay(3000)
                        );
                    },
                    function(failure) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Plugin du job '+job.name+' non executé')
                                .position($scope.getToastPosition())
                                .hideDelay(3000).theme("failure-toast")
                        );
                    });
            }
        }
    ])
    /**
     * job view controller
     */
    .controller('jarvisJobCtrl',
    ['$scope', '$stateParams', 'jobResourceService', 'paramResourceService', 'toastService',
        function($scope, $stateParams, jobResourceService, paramResourceService, toastService) {
    	$scope.params = [];
    	$scope.allParams = [];

        $scope.querySearch = function(query) {
            function createFilterFor(query) {
                var lowercaseQuery = angular.lowercase(query);
                return function filterFn(param) {
                  return (param.value.indexOf(lowercaseQuery) === 0) ||
                      (param.type.indexOf(lowercaseQuery) === 0);
                };
              }

            var results = query ? $scope.allParams.filter(createFilterFor(query)) : [];
            return results;
          }

    	jobResourceService.get($stateParams.id, function(data) {
        	$scope.job = data;
        	toastService.info('Job ' + data.name + '#' + $stateParams.id);
            jobResourceService.params.findAll($stateParams.id, function(params) {
            	$scope.params = params;
            	toastService.info(params.length + ' parameter(s)');
            	/**
            	 * load all params
            	 */
                paramResourceService.findAll(function(params) {
                	$scope.allParams = params;
                	toastService.info(params.length + ' parameter(s) loaded');
                }, toastService.failure);
            }, toastService.failure);
        }, toastService.failure);
    }])
	/**
	 * neo4j view controller
	 */
    .controller('JarvisAppConfCtrl',
    ['$scope', 'jarvisServices', function($scope, jarvisServices) {
        /**
         * loading clients
         */
        jarvisServices.getClients({}, function(data) {
            $scope.jarvis.configuration.clients = data.clients;
        }, function(failure) {
            /**
             * TODO : handle error message
             */
        });
    }
    ]
)
    /**
     * scenario view controller
     */
    .controller('JarvisAppScenarioCtrl',
    ['$scope', 'jarvisServices', 'jarvisJobsResource', '$mdToast',
        function($scope, jarvisServices, jarvisJobsResource, $mdToast) {
            /**
             * some init
             * loading jobs
             */
            jarvisJobsResource.get({}, function (data) {
                for (var index in data) {
                    var params = data[index].params;
                    if (params) {
                        data[index].text = JSON.stringify(data[index].params);
                    }
                }
                $scope.jarvis.configuration.jobs = data;
            }, function (failure) {
                $mdToast.show(
                    $mdToast.simple()
                        .content(failure)
                        .position($scope.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
            });
            /**
             * test plugin associated to this job
             */
            $scope.execute = function(job) {
                $mdToast.show(
                    $mdToast.simple()
                        .content('Test du plugin du job '+job.name)
                        .position($scope.getToastPosition())
                        .hideDelay(3000)
                );
                /**
                 * loading clients
                 */
                jarvisJobsResource.execute(
                    {
                        id: job.id,
                        method: 'test'
                    },
                    {
                        id: job.id,
                        method: 'test'
                    },
                    function(data) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Plugin du job '+job.name+' executé')
                                .position($scope.getToastPosition())
                                .hideDelay(3000)
                        );
                    },
                    function(failure) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Plugin du job '+job.name+' non executé')
                                .position($scope.getToastPosition())
                                .hideDelay(3000).theme("failure-toast")
                        );
                    });
            };
        }
    ]
)
    /**
     * scenario view controller
     */
    .controller('JarvisAppConnectorsCtrl',
    ['$scope', 'jarvisConnectorsResource', '$mdToast',
        function($scope, jarvisConnectorsResource, $mdToast) {
            /**
             * some init
             * loading connectors
             */
            jarvisConnectorsResource.get({},
                function (data) {
                $mdToast.show(
                    $mdToast.simple()
                        .content('Connectors loaded')
                        .position($scope.getToastPosition())
                        .hideDelay(3000)
                );
                $scope.jarvis.connectors = data;
            }, function (failure) {
                $mdToast.show(
                    $mdToast.simple()
                        .content(failure)
                        .position($scope.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
            });
        }
    ]
)

