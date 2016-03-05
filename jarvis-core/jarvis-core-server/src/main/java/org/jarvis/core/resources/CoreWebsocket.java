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

import java.lang.management.ManagementFactory;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

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

	static protected ObjectMapper mapper = new ObjectMapper();

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

	static Thread system = null;
	static Thread runner = null;
	static LinkedBlockingQueue<WebsocketDataBean> queue = new LinkedBlockingQueue<WebsocketDataBean>();

	/**
	 * internal runner to send data on web socket
	 */
	static class WebsocketThread implements Runnable {
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
		
		public class SystemIndicator {
			private double committedVirtualMemorySize;
			private double freePhysicalMemorySize;
			private double freeSwapSpaceSize;
			private double processCpuLoad;
			private double processCpuTime;
			private double systemCpuLoad;
			private double totalPhysicalMemorySize;
			private double totalSwapSpaceSize;

			public SystemIndicator(
					double committedVirtualMemorySize, 
					double freePhysicalMemorySize, 
					double freeSwapSpaceSize, 
					double processCpuLoad, 
					double processCpuTime, 
					double systemCpuLoad, 
					double totalPhysicalMemorySize, 
					double totalSwapSpaceSize) { 
				this.committedVirtualMemorySize = committedVirtualMemorySize;
				this.freePhysicalMemorySize = freePhysicalMemorySize;
				this.freeSwapSpaceSize = freeSwapSpaceSize;
				this.processCpuLoad = processCpuLoad;
				this.processCpuTime = processCpuTime;
				this.systemCpuLoad = systemCpuLoad;
				this.totalPhysicalMemorySize = totalPhysicalMemorySize;
				this.totalSwapSpaceSize = totalSwapSpaceSize;
			}

			public double getCommittedVirtualMemorySize() {
				return committedVirtualMemorySize;
			}

			public double getFreePhysicalMemorySize() {
				return freePhysicalMemorySize;
			}

			public double getFreeSwapSpaceSize() {
				return freeSwapSpaceSize;
			}

			public double getProcessCpuLoad() {
				return processCpuLoad;
			}

			public double getProcessCpuTime() {
				return processCpuTime;
			}

			public double getSystemCpuLoad() {
				return systemCpuLoad;
			}

			public double getTotalPhysicalMemorySize() {
				return totalPhysicalMemorySize;
			}

			public double getTotalSwapSpaceSize() {
				return totalSwapSpaceSize;
			}
		}

		@Override
		public void run() {
			MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
		    ObjectName name = null;
			try {
				name = ObjectName.getInstance(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
			} catch (MalformedObjectNameException | NullPointerException e) {
				logger.error("While sleeping {}", e);
			}

			while (true) {
				try {
				    AttributeList list = null;
					try {
						list = mbs.getAttributes(name, new String[]{ "CommittedVirtualMemorySize", "FreePhysicalMemorySize", "FreeSwapSpaceSize", "ProcessCpuLoad", "ProcessCpuTime", "SystemCpuLoad", "TotalPhysicalMemorySize", "TotalSwapSpaceSize" });
					} catch (InstanceNotFoundException | ReflectionException e) {
						logger.error("While sleeping {}", e);
					}

					broadcast("SystemThread", "1", new SystemIndicator(
							(long) ((Attribute)list.get(0)).getValue(),
							(long) ((Attribute)list.get(1)).getValue(),
							(long) ((Attribute)list.get(2)).getValue(),
							(double) ((Attribute)list.get(3)).getValue(),
							(long) ((Attribute)list.get(4)).getValue(),
							(double) ((Attribute)list.get(5)).getValue(),
							(long) ((Attribute)list.get(6)).getValue(),
							(long) ((Attribute)list.get(7)).getValue()
					));
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("While sleeping {}", e);
				}
			}
		}
	}
}
