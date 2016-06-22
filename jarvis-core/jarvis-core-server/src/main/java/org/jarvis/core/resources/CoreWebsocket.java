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
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jetty.websocket.api.Session;
import org.jarvis.core.model.bean.websocket.WebsocketDataBean;
import org.jarvis.core.websocket.StreamWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
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

	@Autowired
	ThreadPoolTaskScheduler jarvisThreadPoolStatisticsScheduler;

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
	
		SystemIndicator.init();
		Trigger trigger = new CronTrigger("* * * * * *");
		ScheduledFuture<?> sch = jarvisThreadPoolStatisticsScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				broadcast("SystemThread", "1", SystemIndicator.factory());
			}
			
		}, trigger);
		logger.info("Statistics {}", sch);

		/**
		 * internal runner
		 */
		runner = new Thread(new WebsocketThread());
		runner.start();;
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
		logger.debug("Sender {}", sender);
		queue.offer(new WebsocketDataBean(instance, data));
	}

	Thread system = null;
	Thread runner = null;
	static LinkedBlockingQueue<WebsocketDataBean> queue = new LinkedBlockingQueue<>();

	/**
	 * internal runner to send data on web socket
	 */
	class WebsocketThread implements Runnable {
		WebsocketDataBean t = null;

		@Override
		public void run() {
			boolean cont = true;
			while (cont) {
				try {
					t = queue.take();
				} catch (InterruptedException e) {
					logger.error("While taking {}", e);
					cont = false;
				}
				if(cont) {
					/**
					 * broadcast
					 */
					StreamWebSocketHandler.getSessionmap().keySet().stream().filter(Session::isOpen).forEach(session -> {
						try {
							session.getRemote().sendString(mapper.writeValueAsString(t));
						} catch (Exception e) {
							logger.error("While broadcast {} {}", t, e);
						}
					});
				}
			}
		}
	}
}
