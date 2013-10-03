'use strict';

/* App Controllers Module */

/**
 * stories controller
 * applied on /stories context
 */
function StoriesCtrl($scope, Stories, Story) {
	/**
	 * use service to retrieve stories
	 */
	$scope.stories = Stories.post({});

	/**
	 * transform to dialog box
	 */
	$('#report-dialog').dialog({width: 900, autoOpen: true});
	$('#report-story-dialog').dialog({width: 900, autoOpen: true});

	/**
	 * load story in editor
	 */
	$scope.init = function() {
		if($scope.markItUp == true) return;
		$('#report-dialog-story').markItUp('');
		$scope.markItUp = true;
	}

	/**
	 * initialize editor
	 */
	$scope.init();
	
	/**
	 * load story in editor
	 */
	$scope.loadStory = function(item) {
		/**
		 * update editor
		 */
		console.info('Retrieve story ' + item.hash);
		Story.story({hash:item.hash},
				function(data) {
					$scope.edited = data;
					$('#report-dialog-story').text(data.story+'');
				});
	}

	/**
	 * save story in editor
	 */
	$scope.saveStory = function() {
		console.info('Save story ' + $scope.edited.hash);
	}

	/**
	 * load story in editor
	 */
	$scope.executeStory = function() {
		console.info('Execute story ' + $('#report-dialog-story').text());
		Story.execute({rawScript:$('#report-dialog-story').text()},
				function(data) {
					$scope.stdout = data.renderedStdout;
				});
	}
}

function StoryCtrl($scope, $routeParams, Story) {
	/**
	 * use service to retrieve story
	 */
	$scope.story = Story.query({storyId:$routeParams.storyId});
	/**
	 * update editor
	 */
	$('#report-dialog-story').markItUp('');
	$('#report-dialog-story').text($scope.story+'');
}

function MainCtrl($scope, Jarvis) {
	$scope.query = 'hello';

	/**
	 * send message to jarvis
	 */
	$scope.send = function(message) {
		console.info('Send : ' + message);
		Jarvis.send({message:message},
				function(data) {
					console.info(data);
					$scope.answers = data.answers;
					$scope.transactions = data.transactions;
				});
	}
}