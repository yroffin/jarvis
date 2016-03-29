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
    	function($scope, $log, $mdSidenav, $mdDialog, $mdMedia, $location, $state, $window, toastService){
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

        /**
         * sidebar handler
         */
        $scope.sidebar = function() {
          $mdSidenav('left')
            .toggle()
            .then(function () {
              $log.info("toggle left is done");
            });
        }
        
        /**
         * jump to location
         */
        $scope.location = function(target) {
        	$log.info("location", target);
            $location.href(target);
        };

        /**
         * jump to location
         */
        $scope.href = function(target) {
        	$log.info("href", target);
            $window.location.href = target;
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
    .controller('JarvisPrezCtrl.slides',
    	function($scope, $log, $mdDialog, $mdSidenav, $mdMedia, $location, $state, $stateParams, toastService){
        /**
         * `select` put this slide in front.
         */
        $scope.select = function(slide) {
        	$log.info("Slide", slide);
        	var height = $(window).height();
        	var width = $(window).width();

        	var toolbar = $('#toolbar')[0].getBoundingClientRect();

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
        	$scope.hratio = $scope.slides[slide].size.height/height;
        	$scope.wratio = $scope.slides[slide].size.width/width;
        	var ratio = 0;
        	if($scope.wratio > $scope.hratio) {
        		ratio = $scope.wratio;
        	} else {
        		ratio = $scope.hratio;
        	}

        	$scope.slides[slide].current.scale = 1/ratio;
        	$scope.slides[slide].current.zIndex = 0;
        	$scope.current = slide.id;
        	$scope.currentSlide = $scope.slides[slide];
        	$log.info("Slide", slide);
        }

        /**
         * `toNumber` takes a value given as `numeric` parameter and tries to turn
         * it into a number. If it is not possible it returns 0 (or other value
         * given as `fallback`).
         */
        $scope.toNumber = function (numeric, fallback) {
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
            html = html + ' translate3d(' + $scope.toNumber(slide.current.translate.x) + 'px,' + $scope.toNumber(slide.current.translate.y) + 'px,' + $scope.toNumber(slide.current.translate.z) + 'px) ';

            /**
             * rotation
             */
            var rX = " rotateX(" + $scope.toNumber(slide.current.rotate.x) + "deg) ",
            rY = " rotateY(" + $scope.toNumber(slide.current.rotate.y) + "deg) ",
            rZ = " rotateZ(" + $scope.toNumber(slide.current.rotate.z) + "deg) ";
        
            revert ? html = html + ' ' + rZ+rY+rX : html = html + ' ' + rX+rY+rZ;
            
            /**
             * scale
             */
            html = html + ' scale(' + $scope.toNumber(slide.current.scale) + ')';
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

        /**
         * next slide
         */
        $scope.next = function() {
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
        
        $scope.slides = {};
        $scope.slides['home'] = {
        		id: 'home',
        		url: 'partials/slides/home.svg',
        		title: 'General view',
    	        zIndex: -1,
        		size: {
        			width: 1024,
        			height: 768
        		},
        		origin: {
    	            translate : {
    	        		x: -800, y: 200, z: 0
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

        $scope.slides['purpose'] = {
    		id: 'purpose',
    		url: 'partials/slides/purpose.html',
    		title: 'Purpose',
	        zIndex: -1,
    		size: {
    			width: 1024,
    			height: 768
    		},
    		origin: {
	            translate : {
	        		x: -2000, y: 0, z: 0
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

        $scope.slides['structure'] = {
    		id: 'structure',
    		url: 'partials/slides/structure.svg',
    		title: 'Structure',
	        zIndex: -1,
    		size: {
    			width: 1024,
    			height: 768
    		},
    		origin: {
                translate : {
            		x: -2000, y: 2000, z: 0
                },
    	        rotate : {
            		x: 0, y: 0, z: 120
    	        },
    	        perspective : {
    	        	x: 0, y: 0, z: 0
    	        },
    	        scale : 0.01
    		},
    		current:{}
        }

        $scope.slides['setup'] = {
        		id: 'setup',
        		url: 'partials/slides/setup.svg',
        		title: 'Setup configuration',
    	        zIndex: -1,
        		size: {
        			width: 1024,
        			height: 768
        		},
        		origin: {
                    translate : {
                		x: 1500, y: 7000, z: 0
                    },
        	        rotate : {
                		x: 0, y: 0, z: 180
        	        },
        	        perspective : {
        	        	x: 0, y: 0, z: 0
        	        },
        	        scale : 0.001
        		},
        		current:{}
            }

        $scope.slides['sample'] = {
        		id: 'sample',
        		url: 'partials/slides/sample.svg',
        		title: 'Sample configuration',
    	        zIndex: -1,
        		size: {
        			width: 1024,
        			height: 768
        		},
        		origin: {
                    translate : {
                		x: 2000, y: 0, z: 0
                    },
        	        rotate : {
                		x: 0, y: 0, z: 100
        	        },
        	        perspective : {
        	        	x: 0, y: 0, z: 0
        	        },
        	        scale : 0.001
        		},
        		current:{}
            }

        $scope.slides['data-model'] = {
        		id: 'sample',
        		url: 'partials/slides/data-model.svg',
        		title: 'Data model',
    	        zIndex: -1,
        		size: {
        			width: 2048,
        			height: 768
        		},
        		origin: {
                    translate : {
                		x: 2000, y: 0, z: 0
                    },
        	        rotate : {
                		x: 0, y: 0, z: 45
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

    	if($stateParams.slide === undefined) {
            $scope.current = $scope.indexSlides[0];
    	} else {
            $scope.current = $stateParams.slide;
    	}
		$scope.select($scope.current);
    })
