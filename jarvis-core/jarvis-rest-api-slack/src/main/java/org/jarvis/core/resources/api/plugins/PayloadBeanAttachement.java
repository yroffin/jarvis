package org.jarvis.core.resources.api.plugins;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * payload attachement
 */
public class PayloadBeanAttachement {
	/**
	 * title of attachement
	 */
	@JsonProperty("title")
	public String title;
	/**
	 * text attachement
	 */
	@JsonProperty("text")
	public String text;
}
