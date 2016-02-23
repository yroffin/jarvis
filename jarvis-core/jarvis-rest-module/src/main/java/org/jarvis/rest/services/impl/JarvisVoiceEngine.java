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

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.rest.services.CoreRestServices;
import org.jarvis.rest.services.JarvisConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * voide module
 */
@Component
public class JarvisVoiceEngine extends JarvisRestClientImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisVoiceEngine.class);

	/**
	 * constructor
	 */
	@PostConstruct
	public void init() {
		super.init(CoreRestServices.Handler.voice.name(), "jarvis-voice-engine-v1.0b");

		setRenderer(true);
		setSensor(true);
		setCanAnswer(true);

		/**
		 * TODO
		 * fix voices here
		 */
	}

	/**
	 * @param value
	 * @throws IOException
	 */
	public void speak(String value) throws IOException {
	}

	@Override
	public Map<String, Object> onNewMessage(Map<String, Object> message) throws JarvisModuleException {
		try {
			speak((String) message.get("data"));
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", message,
					e.getMessage());
			throw new JarvisModuleException(e);
		}
		return message;
	}
}
