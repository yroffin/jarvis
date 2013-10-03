'use strict';

/* App Modules */

// Create a new module
var myModule = angular.module('jarvisClient', ['jarvisServices','ngRoute']);

myModule.
  config(['$routeProvider', function($routeProvider) {
  console.info('Define routes');
  $routeProvider.
      when('/stories', {templateUrl: 'assets/application/elements/stories.html',   controller: StoriesCtrl}).
      when('/story/:storyId', {templateUrl: 'assets/application/elements/story.html', controller: StoryCtrl}).
      when('/main', {templateUrl: 'assets/application/elements/main.html', controller: MainCtrl}).
      otherwise({redirectTo: '/main'});
}]);
