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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jarvis.core.type.GenericMap;
import org.jarvis.rest.services.impl.JarvisAimlEngine;
import org.jarvis.rest.services.impl.JarvisRemoteExec;
import org.jarvis.rest.services.impl.JarvisVoiceEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

/**
 * core client
 */
@Component
public class CoreRestClient {
	protected static Logger logger = LoggerFactory.getLogger(CoreRestClient.class);

	static private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	JarvisRemoteExec jarvisRemoteExec;

	@Autowired
	JarvisAimlEngine jarvisAimlEngine;

	@Autowired
	JarvisVoiceEngine jarvisVoiceEngine;

	/**
	 * send message to target with POST method
	 * @param client 
	 * 
	 * @param url
	 * @param message
	 * @return JSONResource
	 * @throws IOException
	 */
	static public JSONResource send(Resty client, URI url, GenericMap message) throws IOException {
		client.withHeader("Content-Type", "application/json");
		logger.info("send {}", url);
		return client.json(url, Resty.content(mapper.writeValueAsBytes(message)));
	}

	/**
	 * send message to target with POST method
	 * @param client 
	 * 
	 * @param url
	 * @return JSONResource
	 * @throws IOException
	 */
	static public JSONResource get(Resty client, URI url) throws IOException {
		client.withHeader("Content-Type", "application/json");
		logger.info("get {}", url);
		return client.json(url);
	}

	/**
	 * start main thread
	 * 
	 * @param url
	 * @param href 
	 */
	public void start(String url, String href) {
		InternalThread internal = new InternalThread(url,
				Collections.synchronizedList(new ArrayList<GenericMap>()));

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
		private List<GenericMap> clients;
		private Map<String, String> allConnectors = new HashMap<String,String>();

		/**
		 * constructor
		 * 
		 * @param url
		 * @param clients
		 */
		public InternalThread(String url, List<GenericMap> clients) {
			this.url = url;
			this.clients = clients;
		}

		/**
		 * add new client
		 * 
		 * @param id
		 * @return
		 */
		GenericMap add(JarvisConnector connector, String href) {
			GenericMap message = new GenericMap();
			message.put("id", connector.getId());
			message.put("href", href + "/" + connector.getId());
			message.put("id", connector.getName());
			message.put("isRenderer", connector.isRenderer());
			message.put("isSensor", connector.isSensor());
			message.put("canAnswer", connector.canAnswer());
			clients.add(message);
			return message;
		}

		@Override
		public void run() {
			while (true) {
				try {
					allConnectors.clear();
					/**
					 * load all connectors map
					 */
					try {
						JSONResource remotes = CoreRestClient.get(new Resty(), new URI(url));
						for(int index=0;index < remotes.array().length();index++) {
							JSONObject map = (JSONObject) remotes.array().get(index);
							allConnectors.put((String) map.get("name"),(String) map.get("id"));
						}
					} catch (IOException | URISyntaxException | JSONException e) {
						logger.error("Internal thread {}", e.getMessage());
					}

					/**
					 * periodicaly register this connector to server
					 */
					for (GenericMap msg : clients) {
						String key = allConnectors.get(msg.get("id"));
						if(key != null) {
							CoreRestClient.send(new Resty(), new URI(url + "/"+key+"?task=register"), msg);
						} else {
							logger.warn("No connectors named {}", msg.get("id"));
						}
					}
				} catch (IOException e) {
					logger.warn("Register {}", e.getMessage());
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
