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

package org.jarvis.rest.services.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.jarvis.runtime.ProcessExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * remote exec module
 */
@Component
public class JarvisRemoteExec extends JarvisConnectorImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisRemoteExec.class);

	/**
	 * init this module
	 */
	@PostConstruct
	public void init() {
		renderer = true;
		sensor = true;
		canAnswer = true;
	}

	@Override
	public Map<String, Object> get(Map<String, String> message) throws JarvisModuleException {
		try {
			String result = ProcessExec.execute((String) message.get("data"));
			Map<String, Object> nextMessage = new LinkedHashMap<String, Object>();
			nextMessage.put("data",result);
			nextMessage.put("script",result);
			return nextMessage;
		} catch (Exception e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", message,
					e.getMessage());
			throw new JarvisModuleException(e);
		}
	}
}
