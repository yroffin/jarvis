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

/**
 * controller declaration
 */
angular.module('myImpressDocumentation.controllers', []).controller('ImpressCtrl', [ '$rootScope', '$window', function($scope, $window) {
	/**
	 * init steps
	 * <ul>
	 * <li>index, slide index in local steps array</li>
	 * </ul>
	 */
	$scope.initStep = function(index) {
		var step = $scope.steps[index];
		var dataset = step.dataset;

		var elt = {
			id : step.id,
			el : step,
			translate : {
				x : $scope.tools.integer(dataset.x),
				y : $scope.tools.integer(dataset.y),
				z : $scope.tools.integer(dataset.z)
			},
			rotate : {
				x : $scope.tools.integer(dataset.rotateX),
				y : $scope.tools.integer(dataset.rotateY),
				z : $scope.tools.integer(dataset.rotateZ || dataset.rotate)
			},
			scale : $scope.tools.integer(dataset.scale, 1),
			index : $scope.slides.length
		};

		// store data
		$scope.indexes[elt.id] = elt;
		$scope.slides.push(elt);

		// Fix style
		// Remaps the (0,0) position on the canvas to (-50%, -50%)
		// Then apply modification
		// - translate3d
		// - rotate
		// - scale
		$scope.tools.css(elt.el, {
			position : "absolute",
			width : $scope.config.width + 'px',
			height : $scope.config.height + 'px',
			transform : " " + $scope.tools.translate(elt.translate) + $scope.tools.rotate(elt.rotate) + $scope.tools.scale(elt.scale),
			transformStyle : "preserve-3d"
		});
	}
	/**
	 * select slide api
	 * <ul>
	 * <li>id, id of the slide.</li>
	 * </ul>
	 */
	$scope.selectSlide = function(id) {
		var translateFrom = $scope.indexes[id];

		jQuery.each($scope.slides, function(indexTranslate, totranslate) {
			if (indexTranslate == id)
				return;

			var duration = $scope.tools.integer(duration, $scope.config.transitionDuration);
			var delay = (duration / 2);

			if ($scope.config.debug) {
				console.debug("Translate ", totranslate, " reference ", translateFrom);
			}

			$scope.tools.css(totranslate.el, {
				transform : $scope.tools.translateFrom(totranslate.translate, translateFrom.translate) + $scope.tools.rotate(totranslate.rotate) + $scope.tools.scale(totranslate.scale),
				transitionDuration : duration + "ms",
				transitionDelay : delay + "ms",
			});
		});

		// root div handle rotation and scale
		// elmenents handle translation

		var duration = $scope.tools.integer(duration, $scope.config.transitionDuration);
		var delay = (duration / 3);

		// bind end of first transformation
		$scope.root.bind('oanimationend animationend webkitAnimationEnd transitionend', function() {
			$scope.tools.css($scope.root[0], {
				// inverse rotation and scale for this transition
				transform : "" + $scope.tools.rotateInverse(translateFrom.rotate) + $scope.tools.scale(1 / translateFrom.scale),
				transitionDuration : duration + "ms",
				transitionDelay : delay + "ms",
			});
		});

		// launch transformation
		$scope.tools.css($scope.root[0], {
			// inverse rotation and normal scale for this transition
			transform : "" + $scope.tools.rotateInverse(translateFrom.rotate) + $scope.tools.scale(1),
			transitionDuration : (duration / 2) + "ms",
			transitionDelay : (delay / 2) + "ms",
		});
	}
	/**
	 * presentation bootstrap
	 * 
	 * <ul>
	 * <li>rootId document item id to transform</li>
	 * </ul>
	 */
	$scope.loadPresentation = function(rootId) {
		// fix root property
		$scope.root = $('#' + rootId);

		// some default config values.
		var defaults = {
			width : window.innerWidth,
			height : window.innerHeight,
			maxScale : 1,
			minScale : 0,
			perspective : 1000,
			transitionDuration : 1000,
			debug : false
		};

		// initialize configuration object
		console.info($scope.root);
		var rootData = ($scope.root.dataset == undefined) ? defaults : $scope.root.dataset;
		$scope.config = {
			width : $scope.tools.integer(rootData.width, defaults.width),
			height : $scope.tools.integer(rootData.height, defaults.height),
			maxScale : $scope.tools.integer(rootData.maxScale, defaults.maxScale),
			minScale : $scope.tools.integer(rootData.minScale, defaults.minScale),
			perspective : $scope.tools.integer(rootData.perspective, defaults.perspective),
			transitionDuration : $scope.tools.integer(rootData.transitionDuration, defaults.transitionDuration),
			rootStyle : {
				position : "absolute",
				// size attributes are important for transformation
				// computation
				width : '100%',
				height : '100%',
				transition : "all 0s ease-in-out",
				transformStyle : "preserve-3d"
			}
		};

		// set initial styles
		$scope.tools.css($('body')[0], {
			height : "100%",
			overflow : "hidden"
		});

		// $scope.root identify the center of our presentation
		// all will be done with these reference
		$scope.tools.css($scope.root[0], $scope.config.rootStyle);

		$scope.tools.css($('#info')[0], {
			position : "absolute",
			transform : $scope.tools.translate({
				x : 0,
				y : 0,
				z : 2000
			}) + $scope.tools.rotate({
				x : 0,
				y : 0,
				z : 0
			}) + $scope.tools.scale(1),
			transformStyle : "preserve-3d"
		});

		// load and init steps
		$scope.steps = $('.step');
		$scope.slides = [];
		$scope.indexes = {};
		$scope.steps.each($scope.initStep);

		console.info("Slides loaded", $scope.slides);
	}

	$scope.tools = {
		// `integer` transform (handling undefined) to integer
		integer : function(numeric, fallback) {
			return isNaN(numeric) ? (fallback || 0) : Number(numeric);
		},
		// `translate` builds a translate transform string for given data.
		translate : function(t) {
			return " translate3d(" + t.x + "px," + t.y + "px," + t.z + "px) ";
		},
		// `translate` builds a translate transform string for given data.
		translateInverse : function(t) {
			return " translate3d(" + (-t.x) + "px," + (-t.y) + "px," + (-t.z) + "px) ";
		},
		// `translate` builds a translate transform string for given data.
		translateFrom : function(from, t) {
			return " translate3d(" + (from.x - t.x) + "px," + (from.y - t.y) + "px," + (from.z - t.z) + "px) ";
		},
		// `rotate` builds a rotate transform string for given data.
		// By default the rotations are in X Y Z order that can be reverted by
		// passing
		// `true`
		// as second parameter.
		rotate : function(r, revert) {
			var rX = " rotateX(" + r.x + "deg) ", rY = " rotateY(" + r.y + "deg) ", rZ = " rotateZ(" + r.z + "deg) ";

			return revert ? rZ + rY + rX : rX + rY + rZ;
		},
		rotateInverse : function(r, revert) {
			var rX = " rotateX(" + (-r.x) + "deg) ", rY = " rotateY(" + (-r.y) + "deg) ", rZ = " rotateZ(" + (-r.z) + "deg) ";

			return revert ? rZ + rY + rX : rX + rY + rZ;
		},
		// `scale` builds a scale transform string for given data.
		scale : function(s) {
			return " scale(" + s + ',' + s + ") ";
		},
		// `perspective` builds a perspective transform string for given data.
		perspective : function(p) {
			return " perspective(" + p + "px) ";
		},
		// `pfx` is a function that takes a standard CSS property name as a
		// parameter
		// and returns it's prefixed version valid for current browser it runs
		// in.
		// The code is heavily inspired by Modernizr http://www.modernizr.com/
		pfx : (function() {

			var memory = {};

			memory['transform'] = [ 'transform' ];
			memory['transformStyle'] = [ 'transform-style' ];
			memory['transformOrigin'] = [ 'transform-origin', '-webkit-transform-origin', , '-moz-transform-origin', '-o-transform-origin' ];

			memory['transition'] = [ 'transition', '-webkit-transition', , '-moz-transition', '-o-transition' ];
			memory['transitionDuration'] = [ 'transition-duration' ];
			memory['transitionDelay'] = [ 'transition-delay' ];

			memory['position'] = [ 'position' ];
			memory['overflow'] = [ 'overflow' ];
			memory['left'] = [ 'left' ];
			memory['top'] = [ 'top' ];
			memory['width'] = [ 'width' ];
			memory['height'] = [ 'height' ];

			return function(prop) {
				if (memory[prop] == undefined) {
					console.error("no property ", prop, " is defined");
					return [ prop ];
				}
				return memory[prop];
			};

		})(),
		/**
		 * `css` function applies the styles given in `props` object to the
		 * element given as `el`. It runs all property names through `pfx`
		 * function to make sure proper prefixed version of the property is
		 * used.
		 */
		css : function(el, props) {
			for ( var key in props) {
				if (props.hasOwnProperty(key)) {
					var pkeys = $scope.tools.pfx(key);
					if (pkeys !== undefined) {
						for (var pkey = 0; pkey < pkeys.length; pkey++)
							el.style[pkeys[pkey]] = props[key];
					}
				}
			}
			return el;
		}
	};

} ]);
