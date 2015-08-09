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

angular.module('JarvisApp',['ngMaterial', 'ngMdIcons', 'ngRoute','JarvisApp.services'])
    .config(['$routeProvider', '$locationProvider',
        function($routeProvider, $locationProvider) {
            $routeProvider.
                when('/jobs', {
                    controller: 'JarvisAppJobCtrl',
                    templateUrl: '/js/partials/jobs.html'
                }).
                when('/configuration', {
                    controller: 'JarvisAppConfCtrl',
                    templateUrl: '/js/partials/configuration.html'
                }).
                when('/neo4j', {
                    controller: 'JarvisAppNeo4jCtrl',
                    templateUrl: '/js/partials/neo4j.html'
                }).
                otherwise({
                    redirectTo: '/js/partials/jobs.html'
                });
        }])
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
    ['$scope', '$mdSidenav', '$location', function($scope, $mdSidenav, $location){
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
        $scope.location = function(target) {
            $location.path(target);
        };
    }
    ]
)
    /**
     * job view controller
     */
    .controller('JarvisAppJobCtrl',
    ['$scope', 'jarvisServices', 'jarvisJobsResource', '$mdToast',
        function($scope, jarvisServices, jarvisJobsResource, $mdToast) {
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
            $scope.iconPath = 'https://cdn4.iconfinder.com/data/icons/SOPHISTIQUE/web_design/png/128/our_process_2.png';
            /**
             * some init
             * loading jobs
             */
            jarvisJobsResource.get({}, function(data) {
                for(var index in data) {
                    var params = data[index].params;
                    if(params) {
                        data[index].text = JSON.stringify(data[index].params);
                    }
                }
                $scope.jarvis.configuration.jobs = data;
            }, function(failure) {
                $mdToast.show(
                    $mdToast.simple()
                        .content(failure)
                        .position($scope.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
            });

            /**
             * delete this job
             * @param jobs, all jobs for update ui part after sucess deletion
             * @param job
             */
            $scope.delete = function(jobs, job) {
                /**
                 * loading clients
                 */
                jarvisJobsResource.delete({
                    id : job.id
                }, function(data) {
                    var search = -1;
                    for(var index in jobs) {
                        if(jobs[index].id == job.id) {
                            search = index;
                        }
                    }
                    if(search >= 0) {
                        delete jobs[search];
                        jobs.splice(search, 1);
                    }
                    $mdToast.show(
                        $mdToast.simple()
                            .content('Job ' + job.name + ' supprimé')
                            .position($scope.getToastPosition())
                            .hideDelay(3000)
                    );
                }, function(err) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content(err)
                            .position($scope.getToastPosition())
                            .hideDelay(3000).theme("failure-toast")
                    );
                });
            };

            /**
             * update this job
             * @param job
             */
            $scope.save = function(job) {
                var update = {
                    id : job.id,
                    name : job.name,
                    plugin : job.plugin,
                    cronTime : job.cronTime,
                    started : job.started
                };
                if(job.text) {
                    update.params = JSON.parse(job.text);
                }
                /**
                 * create or update this job
                 */
                jarvisJobsResource.put(update, update,
                    function(data) {
                        job.started = data.started;
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Job ' + job.name + ' enregistré')
                                .position($scope.getToastPosition())
                                .hideDelay(3000)
                        );
                    },
                    function(failure) {
                        $mdToast.show(
                            $mdToast.simple()
                                .content('Job ' + job.name + ' non enregistré')
                                .position($scope.getToastPosition())
                                .hideDelay(3000).theme("failure-toast")
                        );
                    }
                );
            };

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
            };

            /**
             * create a new job
             * @param job
             */
            $scope.new = function() {
                /**
                 * create a new job
                 */
                try {
                    jarvisJobsResource.post({"name": "No name"}, function (data) {
                            jarvisJobsResource.get({}, function (data) {
                                for (var index in data) {
                                    var params = data[index].params;
                                    if (params) {
                                        data[index].text = JSON.stringify(data[index].params);
                                    }
                                }
                                $scope.jarvis.configuration.jobs = data;
                                $mdToast.show(
                                    $mdToast.simple()
                                        .content('Job No name créé')
                                        .position($scope.getToastPosition())
                                        .hideDelay(3000)
                                );
                            }, function (failure) {
                                $mdToast.show(
                                    $mdToast.simple()
                                        .content('Job ' + job.name + ' non créé')
                                        .position($scope.getToastPosition())
                                        .hideDelay(3000).theme("failure-toast")
                                );
                            });
                        },
                        function (failure) {
                            $mdToast.show(
                                $mdToast.simple()
                                    .content('Job non créé')
                                    .position($scope.getToastPosition())
                                    .hideDelay(3000).theme("failure-toast")
                            );
                        });
                } catch(e) {
                    $mdToast.show(
                        $mdToast.simple()
                            .content(''+e)
                            .position($scope.getToastPosition())
                            .hideDelay(3000).theme("failure-toast")
                    );
                }
            }
        }
    ]
)
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
     * neo4j view controller
     */
    .controller('JarvisAppNeo4jCtrl',
    ['$scope', 'jarvisServices', function($scope, jarvisServices) {
        /**
         * loading neo4jdb collections then navigate to target
         */
        jarvisServices.getDbCollections({}, function(data) {
            $scope.jarvis.neo4jdb.collections = data;
        }, function(failure) {
            /**
             * TODO : handle error message
             */
        });
    }
    ]
);
