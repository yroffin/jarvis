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

angular.module('JarvisApp.websocket',['angular-websocket'])
    // WebSocket works as well
    .factory('$notification', [ '$store', '$log', '$websocket', '$window', function($store, $log, $websocket, $window) {
      $log.info("$notification", $window.location);
      // Open a WebSocket connection
      var dataStream = $websocket('ws://'+$window.location.hostname+':'+$window.location.port+'/stream/');

      dataStream.onMessage(function(message) {
    	var entity = JSON.parse(message.data);
    	$store.push(entity.classname, entity.instance, entity.data);
      });

      var methods = {
    	dataStream: dataStream
      };

      return methods;
    }]);
