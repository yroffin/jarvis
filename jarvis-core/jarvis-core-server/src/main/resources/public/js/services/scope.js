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

/**
 * genericScopeService
 */
angular.module('JarvisApp.services.scope', ['JarvisApp.services.generic']).factory('genericScopeService', function($log, genericResourceService) {
  var scope = {
	  resource : function(scope, resource, back, service, link, dataLink, stateParamsId) {
		    /**
		     * Cf. genericResourceService
		     */
		    scope.remove = function(data) {
		    	genericResourceService.scope.entity.remove(function() {scope.go(back)}, resource, data, service);
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.save = function(data) {
		    	genericResourceService.scope.entity.save(resource, data, service);
		    }
		    /**
		     * Cf. genericResourceService
		     */
		    scope.duplicate = function(data) {
		    	genericResourceService.scope.entity.duplicate(function() {scope.go(back)}, resource, data, service);
		    }
		    if(link) {
		    	$log.debug('inject link',link);
			    /**
			     * Cf. genericResourceService
			     */
			    scope.add = function(data) {
			    	$log.debug('link::add',data);
			    	genericResourceService.scope.link.add(stateParamsId,data,dataLink,link,scope.getLink());
				}
			    /**
			     * Cf. genericResourceService
			     */
			    scope.update = function(data) {
			    	$log.debug('link::update',data);
			    	genericResourceService.scope.link.save(stateParamsId,data,link);
				}
			    /**
			     * Cf. genericResourceService
			     */
			    scope.drop = function(data) {
			    	$log.debug('link::drop',data);
			    	genericResourceService.scope.link.remove(stateParamsId,data,link,scope.getLink());
				}
		    }
	  }
  };
  return {
	  scope:scope
  }
});
