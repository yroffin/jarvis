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
	$scope.arborInit = function(parameters, nodeModel, canvas, width, height) {
		/**
		 * init arbor system
		 */
		$scope.arborRenderer = function(canvas) {
			var canvas = $(canvas).get(0)
			var ctx = canvas.getContext("2d");
			var particleSystem

			var that = {
				init : function(system) {
					particleSystem = system
					if (canvas.width < window.innerWidth) {
						canvas.width = window.innerWidth;
					}

					if (canvas.height < window.innerHeight) {
						canvas.height = window.innerHeight;
					}
					particleSystem.screenSize(canvas.width, canvas.height)
					particleSystem.screenPadding(80) // leave an extra 80px
					that.initMouseHandling()
				},

				redraw : function() {
					ctx.fillStyle = "#F0F5F5";
					ctx.fillRect(0, 0, canvas.width, canvas.height)

					particleSystem.eachEdge(function(edge, pt1, pt2) {
						ctx.strokeStyle = "rgba(0,0,0, .333)";
						ctx.lineWidth = 1;
						ctx.beginPath();
						ctx.moveTo(pt1.x, pt1.y);
						ctx.lineTo(pt2.x, pt2.y);
						ctx.stroke();
					})

					particleSystem.eachNode(function(node, pt) {
						var w = 10;
						ctx.fillStyle = "orange";
						ctx.fillRect(pt.x - w / 2, pt.y - w / 2, w, w);
						ctx.fillStyle = "black";
						ctx.font = 'italic 13px sans-serif';
						ctx.fillText(node.name, pt.x + 8, pt.y + 8);
					})
				},

				initMouseHandling : function() {
					var dragged = null;

					var handler = {
						clicked : function(e) {
							var pos = $(canvas).offset();
							var _mouseP = arbor.Point(e.pageX - pos.left, e.pageY - pos.top);
							dragged = particleSystem.nearest(_mouseP);

							var selected = dragged.node.data.name;

							if (dragged && dragged.node !== null) {
								// while we're dragging, don't let physics move
								// the node
								dragged.node.fixed = true
							}

							$(canvas).bind('mousemove', handler.dragged)
							$(window).bind('mouseup', handler.dropped)

							if (selected != undefined)
								$scope.selectSlide(selected);
							return false
						},
						dragged : function(e) {
							var pos = $(canvas).offset();
							var s = arbor.Point(e.pageX - pos.left, e.pageY - pos.top)

							if (dragged && dragged.node !== null) {
								var p = particleSystem.fromScreen(s)
								dragged.node.p = p
							}

							return false
						},

						dropped : function(e) {
							if (dragged === null || dragged.node === undefined)
								return;

							if (dragged.node !== null)
								dragged.node.fixed = false
							dragged.node.tempMass = 1000
							dragged = null
							$(canvas).unbind('mousemove', handler.dragged)
							$(window).unbind('mouseup', handler.dropped)
							var _mouseP = null;
							return false;
						}
					}

					// start listening
					$(canvas).mousedown(handler.clicked);
				},

			}
			return that
		}
		/**
		 * init
		 */
		var sys = arbor.ParticleSystem(); // create the system
		sys.parameters(parameters);
		// with sensible
		// repulsion/stiffness/friction
		sys.parameters({
			gravity : true
		}) // use center-gravity to make the graph settle nicely (ymmv)
		sys.renderer = $scope.arborRenderer('#' + canvas) // our newly created
		sys.graft(nodeModel);
	}

	/**
	 * init steps
	 * <ul>
	 * <li>index, slide index in local steps array</li>
	 * </ul>
	 */
	$scope.initStep = function(index) {
		var step = $scope.steps[index];
		var dataset = step.dataset;

		var depends = "";
		if (dataset.depends != undefined) {
			depends = dataset.depends.split(",");
		}
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
			depends : depends,
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

			var duration = $scope.tools.integer(duration, $scope.config.transitionDuration);
			var delay = Math.floor(duration / 2);

			var translation = JSON.parse(JSON.stringify(translateFrom.translate));
			var rotation = JSON.parse(JSON.stringify(totranslate.rotate));
			var scale = JSON.parse(JSON.stringify(totranslate.scale));

			/**
			 * current selected objet recover its position
			 */
			if (totranslate.id != id) {
				rotation.x = translateFrom.rotate.x;
				rotation.y = translateFrom.rotate.y;
				rotation.z = translateFrom.rotate.z;
				translation.x = 100000 * (Math.random() - 0.5);
				translation.y = 100000 * (Math.random() - 0.5);
				translation.z = -10000;
				scale = 0.1;
			}

			if ($scope.config.debug) {
				console.debug("Translate ", totranslate, " reference ", translateFrom);
			}

			$scope.tools.css(totranslate.el, {
				transform : $scope.tools.scale(scale) + $scope.tools.translateFrom(totranslate.translate, translation) + $scope.tools.rotate(rotation),
				transitionDuration : duration + "ms",
				transitionDelay : delay + "ms",
			});
		});

		// root div handle rotation and scale
		// elmenents handle translation

		var duration = $scope.tools.integer(duration, $scope.config.transitionDuration);
		var delay = Math.floor(duration / 3);

		// bind end of first transformation
		$scope.root.bind('oanimationend animationend webkitAnimationEnd transitionend', function() {
			$scope.tools.css($scope.root[0], {
				// inverse rotation and scale for this transition
				transform : "" + $scope.tools.rotateInverse(translateFrom.rotate) + $scope.tools.scale(1 / translateFrom.scale),
				transitionDuration : Math.floor(duration) + "ms",
				transitionDelay : Math.floor(delay) + "ms",
			});
		});

		// launch transformation
		$scope.tools.css($scope.root[0], {
			// inverse rotation and normal scale for this transition
			transform : "" + $scope.tools.rotateInverse(translateFrom.rotate) + $scope.tools.scale(1),
			transitionDuration : Math.floor(duration / 2) + "ms",
			transitionDelay : Math.floor(delay / 2) + "ms",
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

		// load and init steps
		$scope.steps = $('.step');
		$scope.slides = [];
		$scope.indexes = {};
		$scope.steps.each($scope.initStep);

		console.info("Slides loaded", $scope.slides);
		$scope.selectSlide('index');

		/**
		 * init arbor system
		 */
		var CLR = {
			branch : "#b2b19d",
			code : "orange",
			doc : "#922E00",
			demo : "#a7af00"
		}

		var nodeModel = {
			nodes : {},
			edges : {}
		};

		for (var slideIndex = 0; slideIndex < $scope.slides.length; slideIndex++) {
			nodeModel.nodes[$scope.slides[slideIndex].id] = {
				name : $scope.slides[slideIndex].id
			};
			nodeModel.edges[$scope.slides[slideIndex].id] = {};
			var edges = nodeModel.edges[$scope.slides[slideIndex].id];
			for (var slideDepend = 0; slideDepend < $scope.slides[slideIndex].depends.length; slideDepend++) {
				edges[$scope.slides[slideIndex].depends[slideDepend]] = {
					length : .4
				};
			}
		}

		$scope.arborInit({
			stiffness : 900,
			repulsion : 200,
			gravity : true,
			dt : 0.015
		}, nodeModel, 'viewport', 1024, 768)
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
			memory['transitionDuration'] = [ 'transition-duration', '-webkit-transition-duration', '-moz-transition-duration', '-o-transition-duration' ];
			memory['transitionDelay'] = [ 'transition-delay', '-webkit-transition-delay', '-moz-transition-delay', '-o-transition-delay' ];

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
						for (var pkey = 0; pkey < pkeys.length; pkey++) {
							el.style[pkeys[pkey]] = props[key];
						}
					}
				}
			}
			return el;
		}
	};

} ]);
