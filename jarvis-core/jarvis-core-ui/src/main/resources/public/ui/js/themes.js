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

angular.module('JarvisApp.theme', [ 'ngMaterial' ]).config([ '$mdThemingProvider', function ($mdThemingProvider) {
	$mdThemingProvider.alwaysWatchTheme(true);
    var customPrimary = {
            '50': '#aaeafd',
            '100': '#92e4fc',
            '200': '#79defc',
            '300': '#60d8fb',
            '400': '#47d2fb',
            '500': '#2ECCFA',
            '600': '#15c6f9',
            '700': '#06bbef',
            '800': '#05a7d6',
            '900': '#0594bd',
            'A100': '#c3f0fe',
            'A200': '#dcf7fe',
            'A400': '#f5fdff',
            'A700': '#0480a5'
        };
        $mdThemingProvider
            .definePalette('customPrimary', 
                            customPrimary);

        var customAccent = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#f8fcfe',
            '400': '#e3f4f9',
            '500': '#CEECF5',
            '600': '#b9e4f1',
            '700': '#a4dcec',
            '800': '#8ed3e8',
            '900': '#79cbe4',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#64c3df'
        };
        $mdThemingProvider
            .definePalette('customAccent', 
                            customAccent);

        var customWarn = {
            '50': '#ff8080',
            '100': '#ff6666',
            '200': '#ff4d4d',
            '300': '#ff3333',
            '400': '#ff1a1a',
            '500': '#FF0000',
            '600': '#e60000',
            '700': '#cc0000',
            '800': '#b30000',
            '900': '#990000',
            'A100': '#ff9999',
            'A200': '#ffb3b3',
            'A400': '#ffcccc',
            'A700': '#800000'
        };
        $mdThemingProvider
            .definePalette('customWarn', 
                            customWarn);

        var customBackground = {
            '50': '#ffffff',
            '100': '#ffffff',
            '200': '#ffffff',
            '300': '#ffffff',
            '400': '#ffffff',
            '500': '#F2F2F2',
            '600': '#e5e5e5',
            '700': '#d8d8d8',
            '800': '#cccccc',
            '900': '#bfbfbf',
            'A100': '#ffffff',
            'A200': '#ffffff',
            'A400': '#ffffff',
            'A700': '#b2b2b2'
        };
        $mdThemingProvider
            .definePalette('customBackground', 
                            customBackground);

       $mdThemingProvider.theme('default')
           .primaryPalette('customPrimary')
           .accentPalette('customAccent')
           .warnPalette('customWarn')
           .backgroundPalette('customBackground')
    }]);