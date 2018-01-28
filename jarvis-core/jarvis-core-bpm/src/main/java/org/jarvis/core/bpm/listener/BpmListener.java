package org.jarvis.core.bpm.listener;

import org.jarvis.core.services.CoreMoquette;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * listener
 */
public abstract class BpmListener {
	/**
	 * MQTT broker
	 */
	public static CoreMoquette coreMoquette;
	protected static ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * publish
	 * @param topicName
	 * @param payload
	 */
	protected void publish(String topicName, String payload) {
		if(BpmListener.coreMoquette != null) {
			BpmListener.coreMoquette.publishMostOne(topicName, payload);
		}
	}
}
