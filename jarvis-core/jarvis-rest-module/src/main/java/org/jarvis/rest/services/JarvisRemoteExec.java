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

import javax.annotation.PostConstruct;

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramEvent;
import org.jarvis.client.model.JarvisDatagramExec;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.jarvis.rest.services.impl.JarvisRestClientImpl;
import org.jarvis.runtime.ProcessExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JarvisRemoteExec extends JarvisRestClientImpl implements IJarvisRestClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisRemoteExec.class);

	@PostConstruct
	public void init() {
		super.init(CoreRestServices.Handler.remote.name(), "jarvis-remote-engine-v1.0b");

		setRenderer(true);
		setSensor(true);
		setCanAnswer(true);

		mapper = new ObjectMapper();
	}

	private ObjectMapper mapper;

	@Override
	public JarvisDatagram onNewMessage(JarvisDatagram message) throws JarvisModuleException {
		try {
			String result = ProcessExec.execute(message.request.getData());
			JarvisDatagramExec resultConsole = mapper.readValue(result, JarvisDatagramExec.class);
			JarvisDatagram nextMessage = new JarvisDatagram();
			nextMessage.setCode("event");
			nextMessage.event = new JarvisDatagramEvent();
			nextMessage.event.setData("Result:" + resultConsole.result);
			nextMessage.event.setScript(result);
			return nextMessage;
		} catch (Exception e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", message.request.getData(),
					e.getMessage());
			throw new JarvisModuleException(e);
		}
	}
}
