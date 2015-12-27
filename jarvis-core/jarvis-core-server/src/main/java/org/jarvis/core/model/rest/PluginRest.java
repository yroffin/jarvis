package org.jarvis.core.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * scenario
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PluginRest {
	/**
	 * name
	 */
	@JsonProperty("name")
	public String name;
}
