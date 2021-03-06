package org.jarvis.core.resources;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.common.core.exception.TechnicalException;
import org.jarvis.core.model.bean.websocket.MqttDataBean;
import org.jarvis.core.resources.api.connectors.ApiConnectorResources;
import org.jarvis.core.services.CoreMoquette;
import org.common.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * mqtt broadcaster
 */
@Component
public class CoreMqttSystem {
	protected Logger logger = LoggerFactory.getLogger(CoreMqttSystem.class);

	@Autowired
	Environment env;

	@Autowired
	ApiConnectorResources apiConnectorResources;

	@Autowired
	CoreMoquette coreMoquette;

	@Autowired
	ThreadPoolTaskScheduler jarvisThreadPoolStatisticsScheduler;

	protected Thread runner = null;
	protected ObjectMapper mapper = new ObjectMapper();
	protected MqttClient client;
	private static LinkedBlockingQueue<MqttDataBean> queue = new LinkedBlockingQueue<>();

	/**
	 * spring init
	 */
	@PostConstruct
	public void init() {
		/**
		 * object mapper
		 */
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());

		/**
		 * declare a mqtt client for suscribe to event
		 */
		try {
			/**
			 * Construct an MQTT blocking mode client
			 */
			this.client = new MqttClient(env.getProperty("jarvis.mqtt.url"), Thread.currentThread().getName());
		} catch (MqttException e) {
			logger.error("Unable to set up client: {}", e);
			throw new TechnicalException(e);
		}

		/**
		 * internal runner
		 */
		CoreMqttSystemThread thread = new CoreMqttSystemThread();

		/**
		 * Set this wrapper as the callback handler
		 */
		client.setCallback(thread);
		runner = new Thread(thread);

		SystemIndicator.init();
		Trigger trigger = new CronTrigger("0,10,20,30,40,50 * * * * *");
		ScheduledFuture<?> sch = jarvisThreadPoolStatisticsScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				queue.offer(new MqttDataBean("1", SystemIndicator.factory()));
			}
		}, trigger);
		logger.info("Statistics {}", sch);

		runner.start();
	}

	/**
	 * internal runner to send data on web socket
	 */
	class CoreMqttSystemThread implements MqttCallback, Runnable {
		protected Logger logger = LoggerFactory.getLogger(CoreMqttSystemThread.class);

		public CoreMqttSystemThread() {
		}

		@Override
		public void run() {
			MqttDataBean data = null;
			boolean cont = true;
			while (cont) {
				try {
					data = queue.take();
				} catch (InterruptedException e) {
					logger.error("While taking {}", e);
					Thread.currentThread().interrupt();
					cont = false;
				}
				/**
				 * broadcast
				 */
				try {
					/**
					 * check connexion
					 */
					if (!client.isConnected()) {
						client.connect();

						/**
						 * subscribers
						 */
						try {
							client.subscribe("/api/connectors/#");
							client.subscribe("/collect/#");
							client.subscribe("#");
						} catch (MqttException e) {
							logger.error("Unable to subcribe: {}", e);
						}
					}
					coreMoquette.publishMostOne("/system/core", mapper.writeValueAsString(data));
				} catch (JsonProcessingException | MqttException e) {
					logger.error("While processing {}", e);
				}
			}
		}

		@Override
		public void connectionLost(Throwable cause) {
			logger.warn("connectionLost: {}", cause);
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			if (message.getPayload().length > 0) {
				try {
					switch (message.getPayload()[0]) {
					case '{':
						GenericMap objectValue = mapper.readValue(message.getPayload(), GenericMap.class);
						logger.warn("messageArrived: OBJECT {} {}", topic, objectValue);
						/**
						 * connectors topic
						 */
						if (topic.startsWith("/api/connectors")) {
							apiConnectorResources.register(objectValue);
						}
						break;
					case '[':
						Collection<?> listValue = mapper.readValue(message.getPayload(), Collection.class);
						logger.warn("messageArrived: LIST {} {}", topic, listValue);
						break;
					default:
						String stringValue = new String(message.getPayload());
						logger.warn("messageArrived: STRING {} {}", topic, stringValue);
						break;
					}
				} catch (Exception e) {
					logger.warn("messageArrived: unable to handle {} {}", topic, message);
				}
			} else {
				logger.warn("messageArrived: with zero length {} {}", topic, message);
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			try {
				logger.info("deliveryComplete: {}", token.getMessage());
			} catch (MqttException e) {
				logger.warn("deliveryComplete: {}", e);
			}
		}
	}
}
