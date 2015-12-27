package org.jarvis.core.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * plugin script
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScriptPluginRest {
	/**
	 * name of this plugin
	 */
	@JsonProperty("name")
	public String name;
}
