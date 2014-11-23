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
var myAppServices = angular.module('myApp.services', ['ngResource']);

myAppServices.factory('jarvisServices', ['$resource', 
function ($resource,$windows) {
    return $resource('', {}, {
                   getProperties: {
                        method: 'GET',
                        url: jarvisServicesUrl + '/info/properties',
                        params: {},
                        isArray: false,
                        cache: false
                    },
                   getClients: {
                       method: 'GET',
                       url: jarvisServicesUrl + '/info/clients',
                       params: {},
                       isArray: false,
                       cache: false
                   },
                   send: {
                       method: 'GET',
                       url: jarvisServicesUrl + '/send',
                       params: {},
                       isArray: false,
                       cache: false
                   }
    })}
]);
