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
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * aiml module
 */
@Component
public class JarvisAimlEngine extends JarvisConnectorImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisAimlEngine.class);

	JarvisCoreSystemImpl jarvis;

	private boolean voice;

	/**
	 * constructor
	 */
	@PostConstruct
	public void init() {
		renderer = true;
		sensor = true;
		canAnswer = true;

		/**
		 * implement jarvis system
		 */
		jarvis = new JarvisCoreSystemImpl();
	}

	/**
	 * internal init method
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	@PostConstruct
	private void initialize() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, AimlParsingError, IOException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		logger.info("Default encoding: {}", Charset.defaultCharset().displayName());
		logger.info("Initializing ...");
		jarvis.initialize("baymax", "baymax.txt");
		logger.info("Ready ...");
	}

	@Override
	public Map<String, Object> get(Map<String, String> message) throws JarvisModuleException {
		Map<String, Object> nextMessage = new LinkedHashMap<String, Object>();

		try {
			/**
			 * aiml render
			 */
			List<IAimlHistory> result = jarvis.askSilent((String) message.get("data"));
			for (IAimlHistory value : result) {
				/**
				 * on event per answer, for plugin mecanism
				 */
				nextMessage.put("answer",value.getAnswer());
				nextMessage.put("javascript",value.getJavascript());
			}
			/**
			 * render to local default output
			 */
			logger.warn("Voice status {}", voice);
			if (voice) {
				for (IAimlHistory value : result) {
					jarvis.speak(value.getAnswer());
				}
			}

			return nextMessage;
		} catch (AimlParsingError e) {
			logger.error("Error, while accessing to jarvis with {}", message);
			throw new JarvisModuleException(e);
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {}", message);
			throw new JarvisModuleException(e);
		}
	}
}
