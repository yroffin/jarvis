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

package org.jarvis.core.services;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.resources.SystemIndicator;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

/**
 * code daemon
 */
@Service
@PropertySources({
	@PropertySource(value = "classpath:module.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true)
})
public class CoreRestDaemon {
	
	protected Logger logger = LoggerFactory.getLogger(CoreRestDaemon.class);

	/**
	 * simple json mapper
	 */
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * mqtt client
	 */
	protected MqttClient client;

	@Autowired
	Environment env;

	@Autowired
	ModuleThreadPoolTaskScheduler jarvisThreadPoolTaskScheduler;

	/**
	 * internal cron registry
	 */
	ConcurrentMap<String,ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

	/**
	 * server part
	 */
	@PostConstruct
	public void server() {
		String iface = env.getProperty("jarvis.module.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.module.port"));
		spark.Spark.ipAddress(iface);

		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * init system indicator
		 */
		SystemIndicator.init();
		
		/**
		 * declare a mqtt client for suscribe to event
		 */
		try {
			/**
			 * Construct an MQTT blocking mode client
			 */
			this.client = new MqttClient(env.getProperty("jarvis.mqtt.url"), "rest-client-"+Thread.currentThread().getName());
		} catch (MqttException e) {
			logger.error("Unable to set up client: {}", e);
			throw new TechnicalException(e);
		}

		/**
		 * default api
		 */
		register(CoreMethod.GET, "/api/config", new JarvisConnectorImpl() {
			@Override
			public Map<String, Object> get(Map<String, String> params) {
				Map<String,Object> result = new HashMap<String, Object>();
				result.put("config", SystemIndicator.factory());
				return result;
			}
		});

		/**
		 * wait for initialization
		 */
		spark.Spark.awaitInitialization();
	}
	
	/**
	 * notify handler
	 * @param connector
	 */
	public void notify(JarvisConnector connector) {
		/**
		 * create notifier thread if server interface is defined
		 */
		String serverNotify = env.getProperty("jarvis.server.notify", "0 * * * * *");
		if(serverNotify != null) {
			ConnectorBean bean = new ConnectorBean();
			bean.name = connector.getName();

			/**
			 * iterate on local network interface
			 */
			bean.adress = "http://"+env.getProperty("jarvis.module.interface")+":"+env.getProperty("jarvis.module.port");
			try {
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface inter : Collections.list(interfaces)) {
					/**
					 * ignore loopback
					 */
					if (inter.isLoopback())
					    continue;
					/**
					 * iterate on adress and select IPV4 only
					 */
					for(InterfaceAddress inet : inter.getInterfaceAddresses()) {
						if(inet.getNetworkPrefixLength() == 8
							|| inet.getNetworkPrefixLength() == 24) {
							bean.adress = "http://"+inet.getAddress().getHostAddress()+":"+env.getProperty("jarvis.module.port");
						}
					}
				}
			} catch (SocketException e) {
				throw new TechnicalException(e);
			}

			bean.icon = (String) env.getProperty("jarvis.module.icon", "settings_input_antenna");
			bean.canAnswer = connector.canAnswer();
			bean.isRenderer = connector.isRenderer();
			bean.isSensor = connector.isSensor();

			/**
			 *  register health
			 */
			healthStatus(serverNotify, bean);
		}
	}

	/**
	 * handler
	 * @param client
	 * @param serverUrl
	 * @param bean
	 */
	private void handler(ConnectorBean bean) {
		try {
			/**
			 * check connexion
			 */
			if(!client.isConnected()) {
				client.connect();
			}
			/**
			 * register health status
			 */
			this.client.publish("/api/connectors", mapper.writeValueAsString(bean).getBytes(), 0, false);
		} catch (JsonProcessingException e) {
			logger.error("json parse error {}", e);
		} catch (MqttException e) {
			logger.error("publish error while publish on {}", client.getServerURI());
			logger.error("publish error {} - {}", e, e.getReasonCode());
		}
	}
	
	/**
	 * health status thread schedule
	 * @param client
	 * @param serverUrl
	 * @param cron
	 * @param bean 
	 */
	private void healthStatus(String cron, ConnectorBean bean) {
		/**
		 * initial call
		 */
		handler(bean);

		Trigger trigger = new CronTrigger(cron);

		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				handler(bean);
			}
			
		}, trigger);
		scheduled.put("health-status", sch);
	}

	@SuppressWarnings("unchecked")
	private Object postRequest(Request request, Response response, JarvisConnector resource) throws JarvisModuleException {
		Map<String, Object> input = null;
		try {
			response.header("Content-Type", "application/json");
			input = (Map<String, Object>) mapper.readValue(request.body(), Map.class);
			Map<String, Object> result = resource.post(input, request.params());
			/**
			 * check result
			 */
			if(result != null) {
				return mapper.writeValueAsString(result);
			} else {
				/**
				 * null indicate bad request
				 */
				response.status(HttpServletResponse.SC_BAD_REQUEST);
				return "";
			}
		} catch (IOException e) {
			try {
				response.body(mapper.writeValueAsString(e));
			} catch (JsonProcessingException e1) {
				response.body(e1.getMessage());
			}
			response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return e.getMessage();
		}
	}

	private Object getRequest(Request request, Response response, JarvisConnector resource) throws JarvisModuleException {
		try {
			response.header("Content-Type", "application/json");
			Map<String, Object> result = resource.get(request.params());
			/**
			 * check result
			 */
			if(result != null) {
				return mapper.writeValueAsString(result);
			} else {
				/**
				 * null indicate bad request
				 */
				response.status(HttpServletResponse.SC_BAD_REQUEST);
				return "";
			}
		} catch (IOException e) {
			response.body(e.getMessage());
			response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return e.getMessage();
		}
	}

	/**
	 * register resource
	 * @param method
	 * @param path
	 * @param resource
	 */
	public void register(CoreMethod method, String path, JarvisConnector resource) {
		logger.info("Register {} => {}", method, path);
		switch(method) {
			case POST:
				post(path,
						(request, response) -> postRequest(request, response, resource));
				break;
			case GET:
				get(path,
						(request, response) -> getRequest(request, response, resource));
				break;
		}
	}
}
