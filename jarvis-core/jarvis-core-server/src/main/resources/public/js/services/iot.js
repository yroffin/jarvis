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

/* Services */

angular.module('JarvisApp.services.iot', [])
	.factory('iotResourceService', function(genericResourceService) {
		return {
			iot: genericResourceService.crud(['iots']),
			plugins : genericResourceService.links(['iots'], ['plugins','scripts']),
			iots : genericResourceService.links(['iots'], ['iots']),
			triggers : genericResourceService.links(['iots'], ['triggers'])
		}
	})
;
