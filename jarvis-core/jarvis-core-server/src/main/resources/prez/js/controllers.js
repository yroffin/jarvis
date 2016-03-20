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

angular.module('JarvisPrez.ctrl',[])
    /**
     * main controller
     */
    .controller('JarvisPrezCtrl',
    	function($scope, $log, $mdDialog, $mdSidenav, $mdMedia, $location, $state, toastService){
        /**
         * highlight JS
         */
        hljs.initHighlightingOnLoad();
        
        /**
         * load configuration
         */
        $scope.load = function() {
        }

        /**
         * jump to location
         */
        $scope.location = function(target) {
            $location.path(target);
        };

        /**
         * go to state
         */
        $scope.go = function(target,params) {
            $state.go(target,params);
        };
    })
    .controller('JarvisPrezCtrl.home',
    	function($scope, $log, $mdDialog, $mdSidenav, $mdMedia, $location, $state, toastService){
    })
