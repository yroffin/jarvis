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

import static us.monoid.web.Resty.content;
import static us.monoid.web.Resty.put;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jarvis.client.model.JarvisDatagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import us.monoid.web.Resty;

@Component
public class CoreRestClient {
	protected Logger logger = LoggerFactory.getLogger(CoreRestClient.class);

	static private ObjectMapper mapper = new ObjectMapper();

	/**
	 * send message to target
	 * 
	 * @param url
	 * @param message
	 * @throws IOException
	 */
	static public void send(Resty client, URI url, JarvisDatagram message) throws IOException {
		client.json(url, put(content(mapper.writeValueAsString(message))));
	}

	/**
	 * start main thread
	 * 
	 * @param url
	 */
	public void start(String url) {
		Thread runner = (new Thread(new InternalThread(url)));
		runner.start();
	}

	/**
	 * internal thread for server advertising
	 */
	protected class InternalThread implements Runnable {

		private String url;

		public InternalThread(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			while (true) {
				try {
					CoreRestClient.send(new Resty(), new URI(url), new JarvisDatagram());
				} catch (IOException e) {
				} catch (URISyntaxException e) {
				}
				try {
					logger.warn("sleep");
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		}

	}
}
