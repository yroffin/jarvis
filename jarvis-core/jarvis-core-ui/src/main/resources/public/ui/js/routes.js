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
        /**
         * default state
         */
        $urlRouterProvider.otherwise('/home');
    }])
    .config(['$stateProvider', function($stateProvider) {
        /**
         * now set up the state
         */
        $stateProvider
        .state('token_access', {
            url: '/access_token=:accesToken',
            controller: 'extractTokenCtrl',
            template: ''
        })
        .state('helper-devices', {
            url: '/helper-devices',
            params: {
            	resources: ['crons','triggers','devices','plugins','commands']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('helper-scenarii', {
            url: '/helper-scenarii',
            params: {
            	resources: ['triggers','crons','scenarios','blocks','plugins']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('helper-system', {
            url: '/helper-system',
            params: {
            	resources: ['configurations','notifications','properties','connectors','views']
            },
            controller: 'jarvisWidgetNavigatorCtrl',
            template: '<jarvis-widget-navigator></jarvis-widget-navigator>'
        })
        .state('home', {
            url: '/home',
            controller: 'jarvisWidgetHomeCtrl',
            template: '<jarvis-widget-home></jarvis-widget-home>'
        })
        .state('events', {
            url: '/events',
            controller: 'jarvisWidgetEventsCtrl',
            template: '<jarvis-widget-event></jarvis-widget-event>'
        })
        .state('triggers', {
            url: '/triggers',
            controller: 'jarvisWidgetTriggersCtrl',
            template: '<jarvis-widget-triggers></jarvis-widget-triggers>'
        })
        .state('triggers-by-id', {
            url: '/triggers/:id?tab',
            controller: 'jarvisWidgetTriggerCtrl',
            template: '<jarvis-widget-trigger></jarvis-widget-trigger>'
        })
        .state('notifications', {
            url: '/notifications',
            controller: 'notificationsCtrl',
            template: '<jarvis-widget-notifications></jarvis-widget-notifications>'
        })
        .state('notifications-by-id', {
            url: '/notifications/:id?tab',
            controller: 'notificationCtrl',
            template: '<jarvis-widget-notification></jarvis-widget-notification>'
        })
        .state('devices', {
            url: '/devices',
            controller: 'devicesCtrl',
            template: '<jarvis-widget-devices></jarvis-widget-devices>'
        })
        .state('devices-by-id', {
            url: '/devices/:id?tab',
            controller: 'deviceCtrl',
            template: '<jarvis-widget-device></jarvis-widget-device>'
        })
        .state('plugins', {
            url: '/plugins',
            controller: 'pluginsCtrl',
            template: '<jarvis-widget-plugins></jarvis-widget-plugins>'
        })
        .state('plugins-by-id-script', {
            url: '/plugins/scripts/:id?tab',
            controller: 'pluginScriptCtrl',
            template: '<jarvis-widget-plugin></jarvis-widget-plugin>'
        })
        .state('commands', {
            url: '/commands',
            controller: 'commandsCtrl',
            template: '<jarvis-widget-commands></jarvis-widget-commands>'
        })
        .state('commands-by-id', {
            url: '/commands/:id?tab',
            controller: 'commandCtrl',
            template: '<jarvis-widget-command></jarvis-widget-command>'
        })
        .state('views', {
            url: '/views',
            controller: 'viewsCtrl',
            template: '<jarvis-widget-views></jarvis-widget-views>'
        })
        .state('views-by-id', {
            url: '/views/:id?tab',
            controller: 'viewCtrl',
            template: '<jarvis-widget-view></jarvis-widget-view>'
        })
        .state('configurations', {
            url: '/configurations',
            controller: 'configurationsCtrl',
            template: '<jarvis-widget-configurations></jarvis-widget-configurations>'
        })
        .state('configurations-by-id', {
            url: '/configurations/:id?tab',
            controller: 'configurationCtrl',
            template: '<jarvis-widget-configuration></jarvis-widget-configuration>'
        })
        .state('properties', {
            url: '/properties',
            controller: 'propertiesCtrl',
            template: '<jarvis-widget-properties></jarvis-widget-properties>'
        })
        .state('properties-by-id', {
            url: '/properties/:id?tab',
            controller: 'propertyCtrl',
            template: '<jarvis-widget-property></jarvis-widget-property>'
        })
        .state('connectors', {
            url: '/connectors',
            controller: 'connectorsCtrl',
            template: '<jarvis-widget-connectors></jarvis-widget-connectors>'
        })
        .state('connectors-by-id', {
            url: '/connectors/:id?tab',
            controller: 'connectorCtrl',
            template: '<jarvis-widget-connector></jarvis-widget-connector>'
        })
        .state('snapshots', {
            url: '/snapshots',
            controller: 'snapshotsCtrl',
            template: '<jarvis-widget-snapshots></jarvis-widget-snapshots>'
        })
        .state('snapshots-by-id', {
            url: '/snapshots/:id?tab',
            controller: 'snapshotCtrl',
            template: '<jarvis-widget-snapshot></jarvis-widget-snapshot>'
        })
        .state('crons', {
            url: '/crons',
            controller: 'cronsCtrl',
            template: '<jarvis-widget-crons></jarvis-widget-crons>'
        })
        .state('crons-by-id', {
            url: '/crons/:id?tab',
            controller: 'cronCtrl',
            template: '<jarvis-widget-cron></jarvis-widget-cron>'
        })
        .state('scenarios', {
            url: '/scenarios',
            controller: 'scenariosCtrl',
            template: '<jarvis-widget-scenarios></jarvis-widget-scenarios>'
        })
        .state('scenarios-by-id', {
            url: '/scenarios/:id?tab',
            controller: 'scenarioCtrl',
            template: '<jarvis-widget-scenario></jarvis-widget-scenario>'
        })
        .state('blocks', {
            url: '/blocks',
            controller: 'blocksCtrl',
            template: '<jarvis-widget-blocks></jarvis-widget-blocks>'
        })
        .state('blocks-by-id', {
            url: '/blocks/:id?tab',
            controller: 'blockCtrl',
            template: '<jarvis-widget-block></jarvis-widget-block>'
        })
        ;
    }]);
