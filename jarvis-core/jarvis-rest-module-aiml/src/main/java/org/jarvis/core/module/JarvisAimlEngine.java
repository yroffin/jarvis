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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.services.CoreMethod;
import org.jarvis.core.services.CoreRestDaemon;
import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * aiml module
 */
@Component
public class JarvisAimlEngine extends JarvisConnectorImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisAimlEngine.class);

	@Autowired
	CoreRestDaemon daemon;

	private IAimlCoreEngine engine;

	/**
	 * constructor
	 * @throws IOException 
	 * @throws AimlParsingError 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	@PostConstruct
	public void init() throws AimlParsingError, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		renderer = true;
		sensor = true;
		canAnswer = true;

		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		logger.info("Default encoding: {}", Charset.defaultCharset().displayName());
		logger.info("Initializing ...");

		/**
		 * implement jarvis system
		 */
		engine = instance("baymax.txt");
		engine.setBot("name", "jarvis	");

		/**
		 * register api
		 */
		daemon.register(CoreMethod.POST, "/api/aiml", this);
	}

	/**
	 * internal utility
	 * 
	 * @param resources
	 * @return
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	private IAimlCoreEngine instance(String resources) throws AimlParsingError,
			IOException {
		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream(resources);
		if (is == null) {
			throw new IOException("Unable to find [" + resources
					+ "] in resources paths");
		}
		byte[] b = new byte[is.available()];
		int read = is.read(b);
		if (read == -1) {
			throw new IOException("Unable to read [" + resources
					+ "] in resources paths");
		}
		List<String> res = new ArrayList<>();
		for (String resource : new String(b).replace("\r", "").split("\n")) {
			if (resource.trim().length() > 0) {
				logger.warn("Read resource : " + resource);
				res.add(resource);
			}
		}
		return new AimlCoreEngineImpl(res);
	}

	/**
	 * @param sentence
	 * @return List<IAimlHistory>
	 * @throws AimlParsingError
	 */
	private List<IAimlHistory> chat(String sentence) throws AimlParsingError {
		List<IAimlHistory> answers = engine.ask(sentence);
		return answers;
	}

	/**
	 * @param sentence
	 * @return List<IAimlHistory>
	 * @throws AimlParsingError
	 */
	private List<IAimlHistory> askSilent(String sentence) throws AimlParsingError {
		List<IAimlHistory> answers = chat(sentence);
		return answers;
	}

	@Override
	public Map<String, Object> post(Map<String, Object> input, Map<String, String> params) throws JarvisModuleException {
		Map<String, Object> nextMessage = new LinkedHashMap<String, Object>();

		try {
			/**
			 * aiml render
			 */
			List<IAimlHistory> result = askSilent((String) input.get("data"));
			for (IAimlHistory value : result) {
				/**
				 * on event per answer, for plugin mecanism
				 */
				nextMessage.put("answer",value.getAnswer());
				nextMessage.put("javascript",value.getJavascript());
			}

			return nextMessage;
		} catch (AimlParsingError e) {
			logger.error("Error, while accessing to jarvis with {}", input);
			throw new JarvisModuleException(e);
		}
	}
}
