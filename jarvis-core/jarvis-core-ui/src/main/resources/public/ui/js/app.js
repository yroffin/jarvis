/* 
 * Copyright 2016 Yannick Roffin.
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
     'JarvisApp.directives.files',
     'JarvisApp.directives.widgets',
     /**
      * directives
      */
     'jarvis.directives.navigator',
     'jarvis.directives.command',
     'jarvis.directives.cron',
     'jarvis.directives.scenario',
     'jarvis.directives.trigger',
     'jarvis.directives.configuration',
     'jarvis.directives.notification',
     'jarvis.directives.connector',
     'jarvis.directives.block',
     'jarvis.directives.device',
     'jarvis.directives.event',
     'jarvis.directives.home',
     'jarvis.directives.plugin',
     'jarvis.directives.property',
     'jarvis.directives.snapshot',
     'jarvis.directives.view',
]);
