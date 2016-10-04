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

import org.jarvis.core.SwaggerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

/**
 * main daemon
 */
@Component
@PropertySources({
	@PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true)
})
@SwaggerDefinition(host = "192.168.1.12:8082",
	info = @Info(
			description = "Jarvis",
			version = "v1.0", //
			title = "Jarvis core system",
			contact = @Contact(
					name = "Yannick Roffin", url = "https://yroffin.github.io"
					)
	),
	schemes = { SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS },
	consumes = { "application/json" },
	produces = { "application/json" },
	tags = { @Tag(name = "swagger") }
)
public class JarvisSwaggerServerDaemon {
	protected Logger logger = LoggerFactory.getLogger(JarvisSwaggerServerDaemon.class);

	@Autowired
	Environment env;

	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * start component
	 */
	@PostConstruct
	public void server() {
		
		for(String key : ImmutableList.of(
				"jarvis.user.dir",
				"jarvis.log.dir",
				"jarvis.server.url",
				"jarvis.neo4j.url",
				"jarvis.elasticsearch.url",
				"jarvis.sunset.sunrise.url")) {
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

		spark.Spark.staticFileLocation("swagger");
		spark.Spark.staticFiles.expireTime(600);

		/**
		 * Build swagger json description
		 */
		final String swaggerJson;
		swaggerJson = SwaggerParser.getSwaggerJson("org.jarvis.core");
		spark.Spark.get("/api/swagger", (req, res) -> {
			return swaggerJson;
		});

		spark.Spark.after((request, response) -> {
		    response.header("Content-Encoding", "gzip");
		});
	}
}
