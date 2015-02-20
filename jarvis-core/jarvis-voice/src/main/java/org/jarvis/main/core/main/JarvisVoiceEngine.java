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
import java.io.InputStream;

import javazoom.jl.decoder.JavaLayerException;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.impl.JarvisSocketClientImpl;
import org.jarvis.client.model.JarvisDatagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gtranslate.Audio;
import com.gtranslate.Language;

public class JarvisVoiceEngine extends JarvisSocketClientImpl implements
		IJarvisSocketClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisVoiceEngine.class);

	private Audio voiceManager = null;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisVoiceEngine(String id, String hostName, int portNumber) {
		super(id, hostName, portNumber);

		setRenderer(true);
		setSensor(true);
		setCanAswer(true);

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
				e.printStackTrace();
			}
		} finally {
			if (sound != null)
				sound.close();
		}
	}

	@Override
	public void onConnect() throws Exception {
	}

	@Override
	public void onDisconnect() throws Exception {
	}

	@Override
	public void onNewRequestMessage(JarvisDatagram message) throws IOException {
		super.onNewRequestMessage(message);
		speak(message.request.getData());
	}
}
