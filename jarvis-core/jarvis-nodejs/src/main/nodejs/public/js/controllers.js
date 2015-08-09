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

angular.module('JarvisApp',['ngMaterial', 'ngRoute','JarvisApp.services'])
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
    .config( function($mdThemingProvider){
        // Configure a dark theme with primary foreground yellow
        $mdThemingProvider.theme('docs-dark', 'default')
            .primaryPalette('yellow')
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
        }
        /**
         * load configuration
         */
        $scope.loadConfiguration = function(target) {
            /**
             * loading properties
             */
            jarvisServices.getProperties({}, function(data) {
                /**
                 * deprecated
                 */
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
            /**
             * loading clients
             */
            jarvisServices.getClients({}, function(data) {
                $scope.jarvis.configuration.clients = data.clients;
                console.warn('client', data);
                /**
                 * loading events
                 */
                jarvisServices.getEvents({}, function(data) {
                    console.warn('event', target);
                    $scope.jarvis.configuration.events = data.events;

                }, function(failure) {
                    /**
                     * TODO : handle error message
                     */
                });
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        /**
         * load neo4jdb
         */
        $scope.loadNeo4jdb = function(target) {
            /**
             * loading neo4jdb collections then navigate to target
             */
            jarvisServices.getDbCollections({}, function(data) {
                $scope.jarvis.neo4jdb.collections = data;
                /**
                 * navigate to target
                 */
                $.mobile.navigate(target, {
                    info : "navigate to " + target
                });
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        /**
         * load collection in current scope
         *
         * @param collection
         *            the selected collection
         */
        $scope.loadCollection = function(collection, target) {
            var offset = collection.count - 20;
            if(offset < 0) {
                offset = 0;
            }
            /**
             * loading neo4jdb collections then navigate to target
             */
            jarvisServices.getCollection({
                'database' : collection.db,
                'name' : collection.name,
                'offset' : offset,
                'page' : 20
            }, function(data) {
                var columns = [];
                for ( var column in data[0]) {
                    if (column.indexOf('$') == -1 && column.indexOf('toJSON') == -1) {
                        columns.push(column);
                    }
                }
                $scope.jarvis.neo4jdb.current = collection;
                $scope.jarvis.neo4jdb.collection = data;
                $scope.jarvis.neo4jdb.columns = columns;
                $scope.jarvis.neo4jdb.offset = offset;
                $scope.jarvis.neo4jdb.page = 20;

                /**
                 * navigate to target
                 */
                $.mobile.navigate(target, {
                    info : "navigate to " + target
                });
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        /**
         * send a message to this renderer
         */
        $scope.createJob = function(target) {
            var job = $scope.jarvis.newjob;
            console.info(job);
            /**
             * loading jobs
             */
            jarvisServices.createJob({
                job : job.jobname,
                plugin : job.plugin,
                cronTime : job.cron,
                params : job.args
            }, function(data) {
                /**
                 * loading jobs
                 */
                jarvisServices.getJobs({}, function(data) {
                    console.warn('jobs', target, data);
                    $scope.jarvis.configuration.jobs = data;
                    /**
                     * navigate to target
                     */
                    $.mobile.navigate(target, {
                        info : "navigate to " + target
                    });
                }, function(failure) {
                    /**
                     * TODO : handle error message
                     */
                });
            });
        }
        /**
         * send a message to this renderer
         */
        $scope.send = function(target, element) {
            /**
             * loading clients
             */
            jarvisServices.send({
                target : target,
                message : element
            }, function(data) {
                console.log(data);
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        $scope.testJob = function(target) {
            /**
             * loading clients
             */
            jarvisServices.testJob({
                plugin : target.plugin,
                job : target.job
            }, function(data) {
                console.log(data);
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        $scope.deleteJobByName = function(target) {
            /**
             * loading clients
             */
            jarvisServices.deleteJobByName({
                name : target.job
            }, function(data) {
                /**
                 * loading jobs
                 */
                jarvisServices.getJobs({}, function(data) {
                    console.warn('jobs updated', data);
                    $scope.jarvis.configuration.jobs = data;
                }, function(failure) {
                    /**
                     * TODO : handle error message
                     */
                });
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
        }
        $scope.selectItem = function(item) {
        }
        $scope.selectGroup = function(item) {
        }
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
                for(var job in data) {
                    var params = data[job].params;
                    if(params) {
                        data[job].text = JSON.stringify(data[job].params);
                    }
                }
                $scope.jarvis.configuration.jobs = data;
            }, function(failure) {
                /**
                 * TODO : handle error message
                 */
            });
            /**
             * delete this job
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
                    for(var item in jobs) {
                        if(jobs[item].id == job.id) {
                            search = item;
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
            }
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
                jarvisJobsResource.put(update, update, function(data) {
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
                    });
            }
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
                                for (var job in data) {
                                    var params = data[job].params;
                                    if (params) {
                                        data[job].text = JSON.stringify(data[job].params);
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
