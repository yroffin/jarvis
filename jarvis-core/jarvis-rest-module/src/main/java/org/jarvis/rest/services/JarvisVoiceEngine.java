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

import java.io.IOException;
import java.io.InputStream;

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.jarvis.rest.services.impl.JarvisRestClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gtranslate.Audio;
import com.gtranslate.Language;

import javazoom.jl.decoder.JavaLayerException;

@Component
public class JarvisVoiceEngine extends JarvisRestClientImpl implements IJarvisRestClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisVoiceEngine.class);

	private Audio voiceManager = null;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisVoiceEngine() {
		super("jarvis-voice-engine-v1.0b");

		setRenderer(true);
		setSensor(true);
		setCanAnswer(true);

		/*
		 * The VoiceManager manages all the voices for FreeTTS.
		 */
		voiceManager = Audio.getInstance();

		if (voiceManager == null) {
			logger.error("Cannot initialize audio system");
			return;
		}
	}

	public void speak(String value) throws IOException {
		InputStream sound = null;
		try {
			try {
				if (value != null && value.length() > 0) {
					sound = voiceManager.getAudio(value, Language.FRENCH);
					voiceManager.play(sound);
				}
			} catch (JavaLayerException e) {
				throw new IOException(e);
			} catch (Exception e) {
				throw new IOException(e);
			}
		} finally {
			if (sound != null)
				sound.close();
		}
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
