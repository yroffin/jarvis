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

	@Autowired
	CoreRestClient coreRestClient;

	private ObjectMapper mapper = new ObjectMapper();

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
		voice
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
