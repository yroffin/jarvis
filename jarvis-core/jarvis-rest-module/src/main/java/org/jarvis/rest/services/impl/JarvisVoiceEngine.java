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
import java.util.Locale;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.rest.services.CoreRestServices;
import org.jarvis.rest.services.JarvisConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JarvisVoiceEngine extends JarvisRestClientImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisVoiceEngine.class);

	MaryInterface marytts;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	@PostConstruct
	public void init() {
		super.init(CoreRestServices.Handler.voice.name(), "jarvis-voice-engine-v1.0b");

		setRenderer(true);
		setSensor(true);
		setCanAnswer(true);

		marytts = new LocalMaryInterface();
		marytts.setLocale(Locale.FRENCH);
		Set<String> voices = marytts.getAvailableVoices();
		marytts.setVoice(voices.iterator().next());
	}

	public void speak(String value) throws IOException {
	}

	@Override
	public JarvisDatagram onNewMessage(JarvisDatagram message) throws JarvisModuleException {
		try {
			speak(message.request.getData());
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", message.request.getData(),
					e.getMessage());
			throw new JarvisModuleException(e);
		}
		return message;
	}
}
