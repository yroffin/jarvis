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

angular.module('myApp.controllers', [])
  .controller('BootstrapCtrl', ['$rootScope','$window', 'jarvisServices', function($scope, $window, jarvisServices) {
    /**
     * bootstrap
     */
    $scope.load = function() {
        $scope.theme="b";
        $scope.inventoryItem = {href:''};
        $scope.jarvis = {};
    }
    $scope.loadConfiguration = function() {
    	/**
    	 * load configuration elements
    	 */
        console.log("Configuration loading ...");
        $scope.jarvis.configuration = {};
        /**
         * loading properties
         */
        jarvisServices.getProperties({},
        function(data) {
            console.log(data);
            $scope.jarvis.configuration.properties = data.properties;
            console.log("properties loaded ...");
            /**
             * refresh jquerymobile widget
             */
            setTimeout(function(){
                $("#configuration-properties").table("refresh");
            },100);
        },function(failure) {
            $window.location.href = failure.config.url;
        });
        /**
         * loading clients
         */
        jarvisServices.getClients({},
        function(data) {
            console.log(data);
            $scope.jarvis.configuration.clients = data.clients;
            console.log("clients loaded ...");
            /**
             * refresh jquerymobile widget
             */
            setTimeout(function(){
                $("#configuration-properties").table("refresh");
            },100);
        },function(failure) {
            $window.location.href = failure.config.url;
        });
    }
    $scope.loadGroups = function() {
    }
    $scope.selectItem = function(item) {
    }
    $scope.selectGroup = function(item) {
    }
}])
;