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

/* Ctrls */

angular.module('JarvisApp.ctrl.jobs', ['JarvisApp.services'])
.controller('jobsCtrl', 
	function($scope, $log, jobResourceService, toastService){
	
    /**
     * delete this job
     * @param jobs, all jobs for update ui part after sucess deletion
     * @param job
     */
    $scope.delete = function(jobs, job) {
        /**
         * remove one job
         */
        jobResourceService.delete(job.id, function(data) {
        	toastService.info('job ' + data.name + '#' + data.id +' deleted');
        	
            var search = -1;
            var index = 0;
        	_.forEach(jobs, function(element) {
                if(element.id === job.id) {
                    search = index;
                }
                index++;
            });
            if(search >= 0) {
                delete jobs[search];
                jobs.splice(search, 1);
            }

        }, toastService.failure);
    };

    /**
     * update this job
     * @param job
     */
    $scope.save = function(job) {
        var update = {
            id : job.id,
            name : job.name,
            plugin : job.plugin
        };
        /**
         * create or update this job
         */
        jobResourceService.put(update, function(data) {
                job.started = data.started;
                toastService.info('job ' + data.name + '#' + data.id +' updated');
            }, toastService.failure);
    }

    /**
     * create a new job
     * @param job
     */
    $scope.new = function(jobs) {
        var update = {
            name : "Job name"
        };
        /**
         * create or update this job
         */
        jobResourceService.post(update, function(data) {
                toastService.info('job ' + data.name + '#' + data.id +' created');
                jobs.push(data);
            }, toastService.failure);
    }

    /**
     * update this job
     * @param job
     */
    $scope.putParam = function(param) {
        /**
         * add params
         */
        jobResourceService.param.put(param, function(data) {
                job.started = data.started;
                toastService.info('parameter ' + data.name + '#' + data.id +' added');
            }, toastService.failure);
    }

    /**
     * set started field
     * @param job
     * @param status
     */
    $scope.status = function(job, started) {
        job.started = started;
    };

    /**
     * test plugin associated to this job
     */
    $scope.execute = function(job) {
        $mdToast.show(
            $mdToast.simple()
                .content('Test du plugin du job '+job.name)
                .position($scope.getToastPosition())
                .hideDelay(3000)
        );
        /**
         * loading clients
         */
        jarvisJobsResource.execute(
            {
                id: job.id,
                method: 'test'
            },
            {
                id: job.id,
                method: 'test'
            },
            function(data) {
                $mdToast.show(
                    $mdToast.simple()
                        .content('Plugin du job '+job.name+' executé')
                        .position($scope.getToastPosition())
                        .hideDelay(3000)
                );
            },
            function(failure) {
                $mdToast.show(
                    $mdToast.simple()
                        .content('Plugin du job '+job.name+' non executé')
                        .position($scope.getToastPosition())
                        .hideDelay(3000).theme("failure-toast")
                );
            });
    }

    /**
     * loading jobs
     */
    jobResourceService.findAll(function(data) {
        var arr = [];
    	_.forEach(data, function(element) {
            /**
             * convert internal json params
             */
            arr.push({
            	'id':element.id,
            	'name':element.name,
            	'cronTime':element.cronTime,
            	'plugin':element.plugin,
            });
        });
    	toastService.info(arr.length + ' job(s)');
        $scope.jobs = arr;
    }, toastService.failure);

	$log.debug('jobs-ctrl');
})
.controller('jobCtrl',
	function($scope, $log, $stateParams, jobResourceService, paramResourceService, toastService){
	
    $scope.remove = function(chip) {
    	jobResourceService.params.delete($scope.job.id, chip.id, chip.instance, function(element) {
        	toastService.info('Parameter ' + chip.value + '#' + chip.id + ' :: ' + element.instance + ' removed');
        }, toastService.failure);
    }

    $scope.append = function(chip) {
    	jobResourceService.params.put($scope.job.id, chip.id, function(element) {
        	toastService.info('Parameter ' + chip.value + '#' + chip.id + ' :: ' + element.instance + ' added');
        	chip.instance = element.instance;
        }, toastService.failure);
    }

    $scope.querySearch = function(query) {
        function createFilterFor(query) {
            var lowercaseQuery = angular.lowercase(query);
            return function filterFn(param) {
            	console.error(param);
              return (param.value.indexOf(lowercaseQuery) === 0) ||
                  (param.type.indexOf(lowercaseQuery) === 0);
            };
          }

        var results = query ? $scope.allParams.filter(createFilterFor(query)) : [];
        return results;
      }

	$log.debug('job-ctrl loading');

	$scope.params = [];
	$scope.allParams = [];

	jobResourceService.get($stateParams.id, function(data) {
    	$scope.job = data;
    	toastService.info('Job ' + data.name + '#' + $stateParams.id);
        jobResourceService.params.findAll($stateParams.id, function(params) {
        	$scope.params = params;
        	toastService.info(params.length + ' parameter(s)');
        	/**
        	 * load all params
        	 */
            paramResourceService.findAll(function(params) {
            	$scope.allParams = params;
            	toastService.info(params.length + ' parameter(s) loaded');
            }, toastService.failure);
        }, toastService.failure);
    }, toastService.failure);

	$log.debug('job-ctrl loaded');
})
