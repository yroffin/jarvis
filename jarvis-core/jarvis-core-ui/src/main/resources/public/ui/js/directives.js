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

/* Directives */

angular.module('JarvisApp.directives', [])
    .directive('appVersion', function(version) {
      return function(scope, elm, attrs) {
        elm.text(version);
      };
    })
	.directive('fileModel', function ($parse, $log) {
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var modelSetter = model.assign;
	            
	            element.bind('change', function(){
	                scope.$apply(function(){
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };
	})
	.directive('fileSelect', function ($window, $log) {
	    return {
	        restrict: 'A',
	        require: 'ngModel',
	        link: function (scope, el, attr, ctrl) {
	            var fileReader = new $window.FileReader();
	
	            fileReader.onload = function () {
	            	var base64 = fileReader.result.substr(fileReader.result.lastIndexOf(",")+1);
	            	$log.debug("result:",fileReader.result)
	            	$log.debug("result base64 value:",base64)
	                ctrl.$setViewValue(atob(base64));
	
	                if ('fileLoaded' in attr) {
	                    scope.$eval(attr['fileLoaded']);
	                }
	            };
	
	            fileReader.onprogress = function (event) {
	                if ('fileProgress' in attr) {
	                    scope.$eval(attr['fileProgress'], {'$total': event.total, '$loaded': event.loaded});
	                }
	            };
	
	            fileReader.onerror = function () {
	                if ('fileError' in attr) {
	                    scope.$eval(attr['fileError'], {'$error': fileReader.error});
	                }
	            };
	
	            var fileType = attr['fileSelect'];
	
	            el.bind('change', function (e) {
	                var fileName = e.target.files[0];
	
	                if (fileType === '' || fileType === 'text') {
	                    fileReader.readAsText(fileName, 'UTF-8');
	                } else if (fileType === 'data') {
	                    fileReader.readAsDataURL(fileName);
	                }
	            });
	        }
	    };
	});
