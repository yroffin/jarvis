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

package org.jarvis.core.resources;

import static spark.Spark.webSocket;

import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.jarvis.core.model.bean.websocket.WebsocketDataBean;
import org.jarvis.core.websocket.StreamWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * websocket
 */
@Component
public class CoreWebsocket {
	protected static Logger logger = LoggerFactory.getLogger(CoreWebsocket.class);

	@Autowired
	Environment env;

	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * mount local resource
	 */
	public void mount() {
		/**
		 * mount resources
		 */
		webSocket("/stream", StreamWebSocketHandler.class);

		/**
		 * object mapper
		 */
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	
		/**
		 * internal runner
		 */
		runner = new Thread(new WebsocketThread());
		runner.start();;
		/**
		 * system runner
		 */
		system = new Thread(new SystemThread());
		system.start();;
	}

	/**
	 * broadcast object (with its identifier to client)
	 * 
	 * @param sender
	 *            client identifier
	 * @param instance
	 *            object instance (on server side)
	 * @param data
	 *            object itself
	 */
	public static void broadcast(String sender, String instance, Object data) {
		queue.offer(new WebsocketDataBean(instance, data));
	}

	Thread system = null;
	Thread runner = null;
	static LinkedBlockingQueue<WebsocketDataBean> queue = new LinkedBlockingQueue<WebsocketDataBean>();

	/**
	 * internal runner to send data on web socket
	 */
	class WebsocketThread implements Runnable {
		WebsocketDataBean t = null;

		@Override
		public void run() {
			while (true) {
				try {
					t = queue.take();
				} catch (InterruptedException e) {
					logger.error("While taking {}", e);
				}
				StreamWebSocketHandler.sessionMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
					try {
						session.getRemote().sendString(mapper.writeValueAsString(t));
					} catch (Exception e) {
						logger.error("While broadcast {} {}", t, e);
					}
				});
			}
		}
	}

	/**
	 * internal runner to send data on web socket
	 */
	static class SystemThread implements Runnable {
		
		@Override
		public void run() {
			SystemIndicator.init();
			while (true) {
				try {
					broadcast("SystemThread", "1", SystemIndicator.factory());
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					logger.error("While sleeping {}", e);
				}
			}
		}
	}
}
