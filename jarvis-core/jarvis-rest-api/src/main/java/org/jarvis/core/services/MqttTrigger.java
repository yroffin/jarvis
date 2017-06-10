package org.jarvis.core.services;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import groovy.lang.GroovyShell;

/**
 * mqtt trigger
 */
public class MqttTrigger implements MqttCallback {
	protected Logger logger = LoggerFactory.getLogger(MqttTrigger.class);
	protected MqttClient client;
	private String triggerId;
	private String triggerName;
	private String topic;
	private String body;
	private GroovyShell script;
	
	protected static ObjectMapper mapper;
	protected static CoreEventDaemon coreEventDaemon;
	
	{
		MqttTrigger.mapper = new ObjectMapper();
		MqttTrigger.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MqttTrigger.mapper.registerModule(new JodaModule());
	}

	/**
	 * Mqtt trigger
	 * @param triggerId 
	 * @param triggerName 
	 * @param topic 
	 * @param body 
	 * @param mqttUrl from env.getProperty("jarvis.mqtt.url")
	 */
	public MqttTrigger(String triggerId, String triggerName, String topic, String body, String mqttUrl) {
		this.triggerId = triggerId;
		this.triggerName = triggerName;
		this.topic = topic;
		this.body = body;
		
		/**
		 * declare a mqtt client for suscribe to event
		 */
		try {
			// Construct an MQTT blocking mode client
			this.client = new MqttClient(mqttUrl, "trigger-" + triggerId);
		} catch (MqttException e) {
			logger.error("Unable to set up client: {}", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * connect it
	 */
	public void connect() {
		try {
			// Set this wrapper as the callback handler
			client.setCallback(this);
			client.connect();
			client.subscribe(this.topic);
			
			/**
			 * declare helper in script
			 */
			this.script = new GroovyShell();
			
			logger.info("Subcribe mqtt trigger id: {} name: {} topic: {}", "trigger-" + this.triggerId, this.triggerName, this.topic);
		} catch (MqttException e) {
			logger.error("Unable to connect client: {}", e);
			throw new TechnicalException(e);
		}
	}

	/**
	 * close it
	 */
	public void close() {
		logger.info("Unsubcribe mqtt trigger {} - {}", this.triggerName, "trigger-" + this.triggerId);
		try {
			client.unsubscribe(this.topic);
			client.disconnect();
			client.close();
		} catch (MqttException e) {
			logger.error("Unable to close client: {}", e);
			throw new TechnicalException(e);
		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		logger.warn("connectionLost: {}", cause);
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		try {
			String str = new String(message.getPayload());
			this.script.setVariable("message", str);
			GenericMap json = null;
			try {
				json = mapper.readValue(str, GenericMap.class);
				this.script.setVariable("json", json);
			} catch(Exception e) {
				/**
				 * ignore any exception
				 */
				this.script.setVariable("json", str);
			}
			this.script.setVariable("logger", this.logger);
			boolean result = (boolean) this.script.evaluate(this.body);
			if(result) {
				logger.info("messageArrived: {} {}\n{}\nresult: {}", topic, this.script.getContext().getVariables(), this.body, result);
				coreEventDaemon.post(this.triggerId, topic + " " + str);
			}
		} catch(Exception e) {
			logger.warn("messageArrived with error: {} {}", topic, e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		logger.warn("deliveryComplete: {}", token);
	}

	/**
	 * @return String
	 */
	public String getId() {
		return triggerId;
	}

}
