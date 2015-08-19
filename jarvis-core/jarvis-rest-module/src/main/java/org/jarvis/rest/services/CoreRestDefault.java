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

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRestDefault {
	protected Logger logger = LoggerFactory.getLogger(CoreRestDefault.class);

	@Autowired
	CoreRestClient coreRestClient;

	@Autowired
	JarvisRemoteExec jarvisRemoteExec;

	@Autowired
	JarvisAimlEngine jarvisAimlEngine;

	@Autowired
	JarvisVoiceEngine jarvisVoiceEngine;

	/**
	 * remote service
	 * 
	 * @param message
	 * @return
	 * @throws JarvisModuleException
	 */
	public JarvisDatagram remote(JarvisDatagram message) throws JarvisModuleException {
		return jarvisRemoteExec.onNewMessage(message);
	}

	/**
	 * aiml service
	 * 
	 * @param readValue
	 * @return
	 * @throws JarvisModuleException
	 */
	public JarvisDatagram aiml(JarvisDatagram message) throws JarvisModuleException {
		return jarvisAimlEngine.onNewMessage(message);
	}

	/**
	 * voice service
	 * 
	 * @param message
	 * @return
	 * @throws JarvisModuleException
	 */
	public JarvisDatagram voice(JarvisDatagram message) throws JarvisModuleException {
		return jarvisVoiceEngine.onNewMessage(message);
	}
}
