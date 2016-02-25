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
import javax.sound.sampled.AudioInputStream;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.rest.services.CoreRestServices;
import org.jarvis.rest.services.JarvisConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;

/**
 * voide module
 */
@Component
public class JarvisVoiceEngine extends JarvisRestClientImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisVoiceEngine.class);

	MaryInterface marytts;
	
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
		 * fix voices here
		 */
		try {
			marytts = new LocalMaryInterface();
		} catch (MaryConfigurationException e) {
			throw new TechnicalException(e);
		}

		/**
		 * set catherine voice
		 */
		marytts.setVoice("enst-catherine-hsmm");
	}

	/**
	 * @param value
	 * @throws IOException
	 * @throws SynthesisException 
	 * @throws InterruptedException 
	 */
	public void speak(String value) throws IOException, SynthesisException, InterruptedException {
		AudioInputStream audio = marytts.generateAudio("Bonjour yannick, tu va bien");
		AudioPlayer player = new AudioPlayer(audio);
		player.start();
		player.join();
	}

	@Override
	public Map<String, Object> onNewMessage(Map<String, Object> message) throws JarvisModuleException {
		try {
			try {
				speak((String) message.get("data"));
			} catch (SynthesisException | InterruptedException e) {
				throw new IOException(e);
			}
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", message,
					e.getMessage());
			throw new JarvisModuleException(e);
		}
		return message;
	}
}
