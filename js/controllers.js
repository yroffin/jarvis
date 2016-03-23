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
    	function($scope, $log, $mdDialog,$mdMedia, $location, $state, toastService){
        /**
         * highlight JS
         */
        hljs.initHighlightingOnLoad();
        
        /**
         * load configuration
         */
        $scope.load = function() {
        	$log.debug("load");
        }

        $scope.slides = {};
        $scope.current = 'slide01';

        $scope.slides['slide01'] = {
    		id: 'slide01',
	        zIndex: 0,
    		origin: {
	            translate : {
	        		x: 800, y: 200, z: 0
	            },
		        rotate : {
	        		x: 0, y: 0, z: 180
		        },
		        perspective : {
	        		x: 0, y: 0, z: 0
		        },
		        scale : 0.01
    		},
			current:{}
        }

        $scope.slides['slide02'] = {
    		id: 'slide02',
	        zIndex: 0,
    		origin: {
                translate : {
            		x: 400, y: 250, z: 0
                },
    	        rotate : {
            		x: 0, y: 0, z: 180
    	        },
    	        perspective : {
    	        	x: 0, y: 0, z: 0
    	        },
    	        scale : 0.01
    		},
    		current:{}
        }

        $scope.slides['slide03'] = {
        		id: 'slide03',
    	        zIndex: 0,
        		origin: {
                    translate : {
                		x: 200, y: 250, z: 0
                    },
        	        rotate : {
                		x: 0, y: 0, z: 180
        	        },
        	        perspective : {
        	        	x: 0, y: 0, z: 0
        	        },
        	        scale : 0.01
        		},
        		current:{}
            }

        /**
         * fix all original position of each slide 
         * @param slide
         */
        $scope.indexSlides = [];
        _.each($scope.slides, function(slide) {
        	angular.copy(slide.origin,slide.current);
        	$scope.indexSlides.push(slide.id);
        });

        /**
         * `handler` keyup handler.
         */
        $scope.handler = function(event) {
        	$log.info("handler", event);
        	if(event.keyCode == 102) {
        		var index = _.findIndex($scope.indexSlides, function(slide) {
        			return slide === $scope.current;
        		});
        		if(index == ($scope.indexSlides.length -1)) {
        			$scope.current = $scope.indexSlides[0];
        		} else {
        			$scope.current = $scope.indexSlides[index+1];
        		}
        		$scope.select($scope.current);
        	}
        }
        
        /**
         * `select` put this slide in front.
         */
        $scope.select = function(slide) {
        	$log.debug(slide);
        	var height = $(window).height();
        	var width = $(window).width();

        	/**
        	 * restore all slides positions
        	 */
        	_.each($scope.slides, function(other) {
    			other.current.translate.x = other.origin.translate.x;
    			other.current.translate.y = other.origin.translate.y;
    			other.current.translate.z = other.origin.translate.z;
    			other.current.rotate.x = other.origin.rotate.x;
    			other.current.rotate.y = other.origin.rotate.y;
    			other.current.rotate.z = other.origin.rotate.z;
    			other.current.scale = other.origin.scale;
    			other.current.zIndex = other.origin.zIndex;
            });
        	_.each($scope.slides, function(other) {
       			other.current.translate.x = other.current.translate.x - $scope.slides[slide].current.translate.x + (width / 2);
       			other.current.translate.y = other.current.translate.y - $scope.slides[slide].current.translate.y + (height / 2);
       			other.current.rotate.z = other.current.rotate.z - $scope.slides[slide].current.rotate.z;
            });

        	/**
        	 * select slide and bring it to front
        	 */
        	var rect = $('#'+slide)[0].getBoundingClientRect();
        	var wratio = width/(rect.right - rect.left);
        	var hratio = height/(rect.bottom - rect.top);
        	var ratio = 0;
        	if(wratio < hratio) {
        		ratio = wratio;
        	} else {
        		ratio = hratio;
        	}

        	$scope.slides[slide].current.scale = wratio * $scope.slides[slide].origin.scale;
        	$scope.slides[slide].current.zIndex = 99;
        }

        /**
         * `toNumber` takes a value given as `numeric` parameter and tries to turn
         * it into a number. If it is not possible it returns 0 (or other value
         * given as `fallback`).
         */
        var toNumber = function (numeric, fallback) {
            return isNaN(numeric) ? (fallback || 0) : Number(numeric);
        }
        
        /**
         * `translate` builds a translate transform string for given data.
         */
        $scope.transform3d = function ( slide, revert ) {
        	/**
        	 * translation
        	 */
        	var html = 'translate(-50%,-50%)';
        	
        	/**
        	 * translate 3d
        	 */
            html = html + ' translate3d(' + slide.current.translate.x + 'px,' + slide.current.translate.y + 'px,' + slide.current.translate.z + 'px) ';

            /**
             * rotation
             */
            var rX = " rotateX(" + slide.current.rotate.x + "deg) ",
            rY = " rotateY(" + slide.current.rotate.y + "deg) ",
            rZ = " rotateZ(" + slide.current.rotate.z + "deg) ";
        
            revert ? html = html + ' ' + rZ+rY+rX : html = html + ' ' + rX+rY+rZ;
            
            /**
             * scale
             */
            html = html + ' scale(' + slide.current.scale + ')';
            return html;
        };
        
        /**
         * `perspective3d` how far the element is placed from the view.
         */
        $scope.perspective3d = function ( slide ) {
            return slide.current.perspective.z + "px";
        };
        
        /**
         * `perspectiveOrigin3d` how far the element is placed from the view.
         */
        $scope.perspectiveOrigin3d = function ( slide ) {
            return slide.current.perspective.x + "px " + slide.current.perspective.y + "px";
        };
        
        $scope.settings = function(ev) {
        	$log.debug("Event:", ev);
        	$mdDialog.show({
        		  scope: $scope,
        		  preserveScope: true,
        	      templateUrl: 'tpl-settings',
        	      parent: angular.element(document.body),
        	      targetEvent: ev,
        	      clickOutsideToClose:true,
        	      fullscreen: false
        	}).then(function() {
     		   		$log.warn("Validate settings");
        	   }, function() {
        		   	$log.warn("Cancel settings");
        	   }
        	);
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
