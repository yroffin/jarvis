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
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * code daemon
 */
@Component
@PropertySource("classpath:connector.properties")
public class CoreRestDaemon {

	@Autowired
	CoreRestServices coreRestServices;

	@Autowired
	CoreRestClient coreRestClient;

	@Autowired
	Environment env;

	/**
	 * server part
	 */
	public void server() {
		String url = env.getProperty("jarvis.server.url");
		String iface = env.getProperty("jarvis.connector.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.connector.port"));
		spark.Spark.ipAddress(iface);

		/**
		 * start broadcaster
		 */
		coreRestClient.start(url, "http://" + iface + ":" + port + "/connectors");

		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * mount resources
		 */
		post("/api/connectors/remote",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.remote, request, response));
		post("/api/connectors/aiml",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.aiml, request, response));
		post("/api/connectors/voice",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.voice, request, response));
		post("/api/connectors/dio",
				(request, response) -> coreRestServices.handler(CoreRestServices.Handler.dio, request, response));
	}
}
