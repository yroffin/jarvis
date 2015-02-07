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

public class JarvisClient extends JarvisSocketClientImpl implements
		IJarvisSocketClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisClient.class);

	IJarvisCoreSystem jarvis;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisClient(String hostName, int portNumber) {
		super(hostName, portNumber);

		setName("Jarvis AIML Advisor");
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
		jarvis.initialize("jarvis", "jarvis.txt");
		logger.info("Ready ...");
	}

	@Override
	public void onNewRequestMessage(JarvisDatagram message) throws IOException {
		try {
			List<IAimlHistory> result = jarvis.ask(message.request.getData());
			for (IAimlHistory value : result) {
				/**
				 * on event per answer
				 */
				JarvisDatagram nextMessage = new JarvisDatagram();
				nextMessage.setCode("event");
				nextMessage.event = new JarvisDatagramEvent();
				nextMessage.event.setData(value.getAnswer());
				nextMessage.event.setScript(value.getJavascript());
				sendMessage(nextMessage);
			}
		} catch (AimlParsingError e) {
			logger.error("Error, while accessing to jarvis with {}",
					message.request.getData());
			return;
		}
	}

	/**
	 * standard main procedure
	 * 
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String argv[]) throws Exception {
		JarvisSocketClientImpl client = new JarvisClient("localhost", 5000);
		client.sync();
	}
}
