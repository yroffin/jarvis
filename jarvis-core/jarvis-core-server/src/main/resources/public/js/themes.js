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

/* Cf. https://angular-md-color.com */

angular.module('JarvisApp.theme', [ 'ngMaterial' ]).config(
		function($mdThemingProvider) {
			var customPrimary = {
		        '50': '#ffffff',
		        '100': '#ffffff',
		        '200': '#ffffff',
		        '300': '#ffffff',
		        '400': '#ffffff',
		        '500': '#ffffff',
		        '600': '#f2f2f2',
		        '700': '#e6e6e6',
		        '800': '#d9d9d9',
		        '900': '#cccccc',
		        'A100': '#ffffff',
		        'A200': '#ffffff',
		        'A400': '#ffffff',
		        'A700': '#bfbfbf'
			};
			$mdThemingProvider.definePalette('customPrimary', customPrimary);

			var customAccent = {
		        '50': '#b3c6ff',
		        '100': '#99b3ff',
		        '200': '#809fff',
		        '300': '#668cff',
		        '400': '#4d79ff',
		        '500': '#3366ff',
		        '600': '#1953ff',
		        '700': '#0040ff',
		        '800': '#0039e5',
		        '900': '#0033cc',
		        'A100': '#ccd9ff',
		        'A200': '#e5ecff',
		        'A400': '#ffffff',
		        'A700': '#002db3'
			};
			$mdThemingProvider.definePalette('customAccent', customAccent);

			var customWarn = {
		        '50': '#ff6666',
		        '100': '#ff4d4d',
		        '200': '#ff3333',
		        '300': '#ff1a1a',
		        '400': '#ff0000',
		        '500': '#e60000',
		        '600': '#cc0000',
		        '700': '#b30000',
		        '800': '#990000',
		        '900': '#800000',
		        'A100': '#ff8080',
		        'A200': '#ff9999',
		        'A400': '#ffb3b3',
		        'A700': '#660000'
			};
			$mdThemingProvider.definePalette('customWarn', customWarn);

			var customBackground = {
				'50' : '#404040',
				'100' : '#333333',
				'200' : '#262626',
				'300' : '#1a1a1a',
				'400' : '#0d0d0d',
				'500' : '#000000',
				'600' : '#000000',
				'700' : '#000000',
				'800' : '#000000',
				'900' : '#000000',
				'A100' : '#4d4d4d',
				'A200' : '#595959',
				'A400' : '#666666',
				'A700' : '#000000'
			};
			$mdThemingProvider.definePalette('customBackground',
					customBackground);

			$mdThemingProvider.theme('default')
				.primaryPalette('customPrimary')
				.accentPalette('customAccent')
				.warnPalette('customWarn')
				.backgroundPalette('customBackground', {
					      'default': '900'
					    }
				);

			$mdThemingProvider.theme('default')
				.foregroundPalette['0'] = 'rgba(255,255,255,1)';
			$mdThemingProvider.theme('default')
				.foregroundPalette['1'] = 'rgba(255,255,255,1)';
			$mdThemingProvider.theme('default')
				.foregroundPalette['2'] = 'rgba(255,255,255,1)';
			$mdThemingProvider.theme('default')
				.foregroundPalette['3'] = 'rgba(200,200,200,1)';
		});