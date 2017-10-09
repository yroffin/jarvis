package org.jarvis.core.bpm.listener;

import org.jarvis.core.services.CoreMoquette;

/**
 * listener
 */
public abstract class BpmListener {
	/**
	 * MQTT broker
	 */
	public static CoreMoquette coreMoquette;
	
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
