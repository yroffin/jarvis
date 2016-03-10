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

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

/**
 * core services
 */
@Component
public class CoreRestServices {
	protected Logger logger = LoggerFactory.getLogger(CoreRestServices.class);

	@Autowired
	CoreRestDefault coreRestDefault;

	/**
	 * simple json mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * init this component
	 */
	@PostConstruct
	public void init() {
	}

	/**
	 * modules
	 */
	public enum Handler {
		/**
		 * remote ssh module
		 */
		remote, 
		/**
		 * aiml module
		 */
		aiml,
		/**
		 * voice module
		 */
		voice,
		/**
		 * dio module
		 */
		dio,
		/**
		 * config
		 */
		config
	}

	/**
	 * remote handler
	 * @param h 
	 * 
	 * @param request
	 * @param response
	 * @return Object
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings("unchecked")
	public Object handler(Handler h, Request request, Response response) throws JsonProcessingException {
		try {
			Map<String, Object> result = null;
			response.header("Content-Type", "application/json");
			switch (h) {
			case remote:
				result = coreRestDefault.remote(mapper.readValue(request.body(), Map.class));
				break;
			case aiml:
				result = coreRestDefault.aiml(mapper.readValue(request.body(), Map.class));
				break;
			case voice:
				result = coreRestDefault.voice(mapper.readValue(request.body(), Map.class));
				break;
			case dio:
				result = coreRestDefault.dio(mapper.readValue(request.body(), Map.class));
				break;
			case config:
				return mapper.writeValueAsString(coreRestDefault.config());
			default:
				break;
			}
			return mapper.writeValueAsString(result);
		} catch (IOException e) {
			logger.error("Exception", e);
			response.status(500);
			return mapper.writeValueAsString(e);
		} catch (JarvisModuleException e) {
			logger.error("Exception", e);
			response.status(500);
			return mapper.writeValueAsString(e);
		}
	}
}
