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

package org.jarvis.main.core.main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.impl.JarvisSocketClientImpl;
import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramEvent;
import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarvisAimlEngine extends JarvisSocketClientImpl implements
		IJarvisSocketClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisAimlEngine.class);

	IJarvisCoreSystem jarvis;

	private boolean voice;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisAimlEngine(String id, String hostName, int portNumber,
			boolean voice) {
		super(id, hostName, portNumber);

		this.voice = voice;

		setRenderer(true);
		setSensor(true);
		setCanAswer(true);

		/**
		 * implement jarvis system
		 */
		jarvis = new JarvisCoreSystemImpl();
	}

	@Override
	public void onConnect() throws Exception {
		initialize();
	}

	@Override
	public void onDisconnect() throws Exception {
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
	private void initialize() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, AimlParsingError,
			IOException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class
				.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		logger.info("Default encoding: {}", Charset.defaultCharset()
				.displayName());
		logger.info("Initializing ...");
		jarvis.initialize("baymax", "baymax.txt");
		logger.info("Ready ...");
	}

	@Override
	public void onNewRequestMessage(JarvisDatagram message) throws IOException {
		super.onNewRequestMessage(message);
		try {
			/**
			 * aiml render
			 */
			List<IAimlHistory> result = jarvis.askSilent(message.request
					.getData());
			for (IAimlHistory value : result) {
				/**
				 * on event per answer, for plugin mecanism
				 */
				JarvisDatagram nextMessage = new JarvisDatagram();
				nextMessage.setCode("event");
				nextMessage.event = new JarvisDatagramEvent();
				nextMessage.event.setData(value.getAnswer());
				nextMessage.event.setScript(value.getJavascript());
				nextMessage.event.setTarget(message.request.getFrom());
				sendMessage(nextMessage);
				/**
				 * on event per answer, for plugin mecanism
				 */
				JarvisDatagram answerMessage = new JarvisDatagram();
				answerMessage.setCode("event");
				answerMessage.event = new JarvisDatagramEvent();
				answerMessage.event.setData(value.getAnswer());
				answerMessage.event.setScript(value.getJavascript());
				answerMessage.event.setTarget(message.request.getFrom());
				sendMessage(answerMessage);
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
		} catch (AimlParsingError e) {
			logger.error("Error, while accessing to jarvis with {}",
					message.request.getData());
			return;
		}
	}
}
