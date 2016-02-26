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

import org.jarvis.dio.DioHelper;
import org.jarvis.rest.services.CoreRestServices;
import org.jarvis.rest.services.JarvisConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * voide module
 */
@Component
public class JarvisDioEngine extends JarvisRestClientImpl implements JarvisConnector {

	protected Logger logger = LoggerFactory.getLogger(JarvisDioEngine.class);
	DioHelper dioHelper;

	/**
	 * constructor
	 */
	@PostConstruct
	public void init() {
		super.init(CoreRestServices.Handler.voice.name(), "jarvis-voice-engine-v1.0b");

		setRenderer(true);
		setSensor(false);
		setCanAnswer(false);
		
		dioHelper = new DioHelper();
	}

	/**
	 * @param pin 
	 * @param sender 
	 * @param interruptor 
	 * @param on 
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 */
	public void action(String pin, String sender, String interruptor, boolean on) throws IOException, NumberFormatException, InterruptedException {
		if(on) {
			dioHelper.pin(Integer.parseInt(pin)).sender(Integer.parseInt(sender)).interruptor(Integer.parseInt(interruptor)).switchOn();
		} else {
			dioHelper.pin(Integer.parseInt(pin)).sender(Integer.parseInt(sender)).interruptor(Integer.parseInt(interruptor)).switchOff();
		}
	}

	@Override
	public Map<String, Object> onNewMessage(Map<String, Object> message) throws JarvisModuleException {
		try {
			try {
				action((String) message.get("pin"), (String) message.get("sender"), (String) message.get("interruptor"), Boolean.parseBoolean((String) message.get("on")));
			} catch (NumberFormatException | InterruptedException e) {
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
