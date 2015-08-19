/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.rest.services;

import static spark.Spark.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRestDaemon {

	@Autowired
	CoreRestServices coreRestServices;

	@Autowired
	CoreRestClient coreRestClient;

	public void server(String url) {
		/**
		 * start broadcaster
		 */
		coreRestClient.start(url);

		/**
		 * mount resources
		 */
		post("/module/remote",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.remote, request, response));
		post("/module/aiml",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.aiml, request, response));
		post("/module/voice",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.voice, request, response));
	}
}
