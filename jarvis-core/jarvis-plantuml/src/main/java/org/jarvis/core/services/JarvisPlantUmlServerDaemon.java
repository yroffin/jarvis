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

package org.jarvis.core.services;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * main daemon
 */
@Component
@PropertySources({
	@PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true)
})
public class JarvisPlantUmlServerDaemon {
	protected Logger logger = LoggerFactory.getLogger(JarvisPlantUmlServerDaemon.class);

	@Autowired
	Environment env;

	@Autowired
	PlantUmlService plantUmlService;
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * start component
	 */
	@PostConstruct
	public void server() {
		
		for(String key : ImmutableList.of(
				"jarvis.user.dir",
				"jarvis.log.dir",
				"jarvis.server.url")) {
			logger.info("{} = {}", key, env.getProperty(key));
		}
		
		String iface = env.getProperty("jarvis.server.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.server.port"));
		spark.Spark.ipAddress(iface);
		spark.Spark.threadPool(Integer.parseInt(env.getProperty("jarvis.server.pool.thread","8")));
		
		/**
		 * port
		 */
		spark.Spark.port(port);

		spark.Spark.staticFileLocation("plantuml");
		spark.Spark.staticFiles.expireTime(0);

		/**
		 * Build plantuml uml api
		 */
		spark.Spark.post("/api/plantuml", (req, res) -> {
			return plantUmlService.svg(req.body());
		});

		spark.Spark.after((request, response) -> {
		    response.header("Content-Encoding", "gzip");
		});
	}
}
