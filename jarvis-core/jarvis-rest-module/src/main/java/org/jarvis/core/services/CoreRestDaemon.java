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

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.jarvis.core.resources.SystemIndicator;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

/**
 * code daemon
 */
@Service
@PropertySources({
	@PropertySource(value = "classpath:module.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true)
})
public class CoreRestDaemon {
	protected Logger logger = LoggerFactory.getLogger(CoreRestDaemon.class);

	/**
	 * simple json mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	Environment env;

	/**
	 * server part
	 */
	@PostConstruct
	public void server() {
		String iface = env.getProperty("jarvis.module.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.module.port"));
		spark.Spark.ipAddress(iface);

		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * init system indicator
		 */
		SystemIndicator.init();
		
		/**
		 * default api
		 */
		register(CoreMethod.GET, "/api/config", new JarvisConnectorImpl() {
			@Override
			public Map<String, Object> get(Map<String, String> params) {
				Map<String,Object> result = new HashMap<String, Object>();
				result.put("config", SystemIndicator.factory());
				return result;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private Object postRequest(Request request, Response response, JarvisConnector resource) throws JarvisModuleException {
		Map<String, Object> input = null;
		try {
			response.header("Content-Type", "application/json");
			input = (Map<String, Object>) mapper.readValue(request.body(), Map.class);
			Map<String, Object> result = resource.post(input, request.params());
			/**
			 * check result
			 */
			if(result != null) {
				return mapper.writeValueAsString(result);
			} else {
				/**
				 * null indicate bad request
				 */
				response.status(HttpServletResponse.SC_BAD_REQUEST);
				return "";
			}
		} catch (IOException e) {
			try {
				response.body(mapper.writeValueAsString(e));
			} catch (JsonProcessingException e1) {
				response.body(e1.getMessage());
			}
			response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return e.getMessage();
		}
	}

	private Object getRequest(Request request, Response response, JarvisConnector resource) throws JarvisModuleException {
		try {
			response.header("Content-Type", "application/json");
			Map<String, Object> result = resource.get(request.params());
			/**
			 * check result
			 */
			if(result != null) {
				return mapper.writeValueAsString(result);
			} else {
				/**
				 * null indicate bad request
				 */
				response.status(HttpServletResponse.SC_BAD_REQUEST);
				return "";
			}
		} catch (IOException e) {
			response.body(e.getMessage());
			response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return e.getMessage();
		}
	}

	/**
	 * register resource
	 * @param method
	 * @param path
	 * @param resource
	 */
	public void register(CoreMethod method, String path, JarvisConnector resource) {
		logger.info("Register {} => {}", method, path);
		switch(method) {
			case POST:
				post(path,
						(request, response) -> postRequest(request, response, resource));
				break;
			case GET:
				get(path,
						(request, response) -> getRequest(request, response, resource));
				break;
		}
	}
}
