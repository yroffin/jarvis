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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramClient;
import org.jarvis.client.model.JarvisDatagramSession;
import org.jarvis.rest.services.impl.JarvisAimlEngine;
import org.jarvis.rest.services.impl.JarvisRemoteExec;
import org.jarvis.rest.services.impl.JarvisVoiceEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.monoid.web.Resty;

@Component
public class CoreRestClient {
	protected Logger logger = LoggerFactory.getLogger(CoreRestClient.class);

	static private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	JarvisRemoteExec jarvisRemoteExec;

	@Autowired
	JarvisAimlEngine jarvisAimlEngine;

	@Autowired
	JarvisVoiceEngine jarvisVoiceEngine;

	/**
	 * send message to target
	 * 
	 * @param url
	 * @param message
	 * @throws IOException
	 */
	static public void send(Resty client, URI url, JarvisDatagram message) throws IOException {
		client.withHeader("Content-Type", "application/json");
		client.json(url, Resty.content(mapper.writeValueAsBytes(message)));
	}

	/**
	 * start main thread
	 * 
	 * @param url
	 */
	public void start(String url, String href) {
		InternalThread internal = new InternalThread(url,
				Collections.synchronizedList(new ArrayList<JarvisDatagram>()));

		/**
		 * add internal rest id and its href
		 */
		internal.add(jarvisRemoteExec, href);
		internal.add(jarvisAimlEngine, href);
		internal.add(jarvisVoiceEngine, href);

		Thread runner = (new Thread(internal));
		runner.start();
	}

	/**
	 * internal thread for server advertising
	 */
	protected class InternalThread implements Runnable {

		private String url;
		private List<JarvisDatagram> clients;

		/**
		 * constructor
		 * 
		 * @param url
		 * @param clients
		 */
		public InternalThread(String url, List<JarvisDatagram> clients) {
			this.url = url;
			this.clients = clients;
		}

		/**
		 * add new client
		 * 
		 * @param id
		 * @return
		 */
		JarvisDatagram add(JarvisConnector connector, String href) {
			JarvisDatagram message = new JarvisDatagram();
			message.session = new JarvisDatagramSession();
			message.session.client = new JarvisDatagramClient();
			message.session.client.id = connector.getId();
			message.session.client.href = href + "/" + connector.getId();
			message.session.client.name = connector.getName();
			message.session.client.isRenderer = connector.isRenderer();
			message.session.client.isSensor = connector.isSensor();
			message.session.client.canAnswer = connector.canAnswer();
			clients.add(message);
			return message;
		}

		@Override
		public void run() {
			while (true) {
				try {
					/**
					 * periodicaly register this connector to server
					 */
					for (JarvisDatagram msg : clients) {
						URI uri = new URI(url + "/task?method=register");
						CoreRestClient.send(new Resty(), uri, msg);
					}
				} catch (IOException e) {
				} catch (URISyntaxException e) {
				}
				try {
					/**
					 * sleeping
					 */
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}

	}
}
