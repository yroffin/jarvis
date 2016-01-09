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
     'restangular',
     'ui.router',
     'ui.router.router',
     'pascalprecht.translate',
     'ngCookies',
     'hljs',
     'JarvisApp.config',
     'JarvisApp.routes',
     'JarvisApp.services',
     'JarvisApp.services.filter',
     'JarvisApp.services.plugin',
     'JarvisApp.services.command',
     'JarvisApp.services.view',
     'JarvisApp.services.iot',
     'JarvisApp.directives.widgets',
     /**
      * controllers
      */
     'JarvisApp.ctrl.plugins',
     'JarvisApp.ctrl.commands',
     'JarvisApp.ctrl.jobs',
     'JarvisApp.ctrl.iots',
     'JarvisApp.ctrl.views',
     'JarvisApp.ctrl.scenarios',
     'JarvisApp.ctrl.home'
]);
