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
 * pluginResourceService
 */
angular.module('JarvisApp.services.plugin', []).factory('pluginResourceService', function($log, Restangular, filterService, commandResourceService, genericResourceService) {
  var plugins = {
	        /**
			 * base services : findAll, delete, put and post
			 */
			findAll: function(callback, failure) {
                var arr = [];
                var plugins = ['scripts'];
				var done = _.after(plugins.length, function(loaded) {
					callback(loaded);
				});
				_.forEach(plugins, function(plugin) {
					Restangular.all('plugins').all(plugin).getList().then(function(elements) {
		            	_.forEach(elements, function(element) {
		                    arr.push(filterService.plain(element));
		                });
		            	done(arr);
					},function(errors){
						failure(errors);
					});
				});
			}
  };
  return {
	    plugins: plugins,
	    scripts: genericResourceService.crud(['plugins','scripts']),
	    commands : genericResourceService.links(['plugins','scripts'], ['commands'])
  }
});
