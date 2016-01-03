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
    .config(function($urlRouterProvider) {
        /**
         * default state
         */
        $urlRouterProvider.otherwise('/iots');
    })
    .config(function($stateProvider) {
        /**
         * now set up the state
         */
        $stateProvider
        .state('jobs', {
            url: '/jobs',
            controller: 'jobsCtrl',
            templateUrl: '/ui/js/partials/jobs/page.html'
        })
        .state('jobs-by-id', {
            url: '/jobs/:id',
            controller: 'jobCtrl',
            templateUrl: '/ui/js/partials/jobs/job/page.html'
        })
        .state('configuration', {
            url: '/configuration',
            controller: 'JarvisAppCtrl',
            templateUrl: '/ui/js/partials/configuration.html'
        })
        .state('scenario', {
            url: '/scenario',
            controller: 'JarvisAppCtrl',
            templateUrl: '/ui/js/partials/scenario.html'
        })
        .state('connectors', {
            url: '/connectors',
            controller: 'JarvisAppCtrl',
            templateUrl: '/ui/js/partials/connectors.html'
        })
        .state('iots', {
            url: '/iots',
            controller: 'iotsCtrl',
            templateUrl: '/ui/js/partials/iots/page.html'
        })
        .state('iots-by-id', {
            url: '/iots/:id',
            controller: 'iotCtrl',
            templateUrl: '/ui/js/partials/iots/iot/page.html'
        })
        .state('plugins', {
            url: '/plugins',
            controller: 'pluginsCtrl',
            templateUrl: '/ui/js/partials/plugins/page.html'
        })
        .state('plugins-by-id-script', {
            url: '/plugins/scripts/:id',
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
        ;
    })
