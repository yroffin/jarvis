package org.jarvis.core.resources.api.plugins;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * payload
 */
public class PayloadBean {
	/**
	 * response type
	 */
	@JsonProperty("response_type")
	public String response_type = "in_channel";
	/**
	 * text
	 */
	@JsonProperty("text")
	public String text;
	/**
	 * attachements
	 */
	@JsonProperty("attachments")
	public PayloadBeanAttachement[] attachments;
}
