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

import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.resources.SystemIndicator;
import org.jarvis.rest.services.impl.JarvisAimlEngine;
import org.jarvis.rest.services.impl.JarvisDioEngine;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.jarvis.rest.services.impl.JarvisRemoteExec;
import org.jarvis.rest.services.impl.JarvisVoiceEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rest core
 */
@Component
public class CoreRestDefault {
	protected Logger logger = LoggerFactory.getLogger(CoreRestDefault.class);

	@Autowired
	JarvisRemoteExec jarvisRemoteExec;

	@Autowired
	JarvisAimlEngine jarvisAimlEngine;

	@Autowired
	JarvisVoiceEngine jarvisVoiceEngine;

	@Autowired
	JarvisDioEngine jarvisDioEngine;

	/**
	 * init this component
	 */
	@PostConstruct
	public void init() {
		SystemIndicator.init();
	}

	/**
	 * remote service
	 * 
	 * @param message
	 * @return JarvisDatagram
	 * @throws JarvisModuleException
	 */
	public Map<String, Object> remote(Map<String, Object> message) throws JarvisModuleException {
		return jarvisRemoteExec.onNewMessage(message);
	}

	/**
	 * aiml service
	 * @param message 
	 * 
	 * @return JarvisDatagram
	 * @throws JarvisModuleException
	 */
	public Map<String, Object> aiml(Map<String, Object> message) throws JarvisModuleException {
		return jarvisAimlEngine.onNewMessage(message);
	}

	/**
	 * voice service
	 * 
	 * @param message
	 * @return JarvisDatagram
	 * @throws JarvisModuleException
	 */
	public Map<String, Object> voice(Map<String, Object> message) throws JarvisModuleException {
		return jarvisVoiceEngine.onNewMessage(message);
	}

	/**
	 * dio service
	 * @param message
	 * @return Map<String, Object>
	 * @throws JarvisModuleException 
	 */
	public Map<String, Object> dio(Map<String, Object> message) throws JarvisModuleException {
		return jarvisDioEngine.onNewMessage(message);
	}

	/**
	 * @return Map<String, Object>
	 * @throws JarvisModuleException 
	 */
	public SystemIndicator config() throws JarvisModuleException {
		return SystemIndicator.factory();
	}
}
