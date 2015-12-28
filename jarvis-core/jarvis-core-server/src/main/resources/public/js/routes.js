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
            controller: 'jobsDirectiveCtrl',
            template: '<jobs-directive />'
        })
        .state('jobs-id', {
            url: '/jobs/:id',
            controller: 'jobDirectiveCtrl',
            template: '<job-directive />'
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
            controller: 'iotsDirectiveCtrl',
            template: '<iots-directive />'
        })
        .state('iots-id', {
            url: '/iots/:id',
            controller: 'iotDirectiveCtrl',
            template: '<iot-directive />'
        })
        .state('plugins', {
            url: '/plugins',
            controller: 'pluginsDirectiveCtrl',
            template: '<plugins-directive />'
        })
        .state('plugins-id-script', {
            url: '/plugins/scripts/:id',
            controller: 'pluginScriptDirectiveCtrl',
            templateUrl: '/ui/js/partials/directives/plugins/script/page.html'
        })
        ;
    })
