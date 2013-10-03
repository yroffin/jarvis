'use strict';

/* App Services module */

angular.module('jarvisServices', ['ngResource'], 
	function($provide) {
		/**
		 * jarvis provider
		 */
		console.info('Register jarvis services');
		$provide.factory('Jarvis', function($resource) {
			return $resource('', {}, {
				send: {url: '/rest/send', method:'POST', isArray:false, cache:false},
				execute: {url: '/rest/execute', method:'POST', isArray:false, cache:false}
			});
		});
	});
