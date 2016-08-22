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

package org.jarvis.core.module;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.services.CoreMethod;
import org.jarvis.core.services.CoreRestDaemon;
import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.jarvis.runtime.ProcessExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * voide module
 */
@Component
public class JarvisExecEngine extends JarvisConnectorImpl implements JarvisConnector {

	@Autowired
	Environment env;

	@Autowired
	CoreRestDaemon daemon;

	protected Logger logger = LoggerFactory.getLogger(JarvisExecEngine.class);
	
	/**
	 * constructor
	 */
	@PostConstruct
	public void init() {
		super.init(env);

		renderer = true;
		sensor = true;
		canAnswer = true;

		/**
		 * register api
		 */
		daemon.register(CoreMethod.POST, "/api/exec", this);
	}

	@Override
	public Map<String, Object> post(Map<String, Object> input, Map<String, String> params) throws JarvisModuleException {
		try {
			String result = ProcessExec.execute((String) input.get("data"));
			Map<String, Object> nextMessage = new LinkedHashMap<String, Object>();
			nextMessage.put("data",result);
			nextMessage.put("script",result);
			return nextMessage;
		} catch (Exception e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", input,
					e.getMessage());
			throw new JarvisModuleException(e);
		}
	}
}
