package org.jarvis.core.model.rest.plugin;

import org.jarvis.core.model.rest.GenericEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * scenario
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PluginRest extends GenericEntity {
	/**
	 * name
	 */
	@JsonProperty("name")
	public String name;
	/**
	 * type
	 */
	@JsonProperty("type")
	public String type;
	/**
	 * icon
	 */
	@JsonProperty("icon")
	public String icon;
	/**
	 * owner
	 */
	@JsonProperty("owner")
	public String owner;
	/**
	 * active
	 */
	@JsonProperty("active")
	public boolean active;
	/**
	 * visible
	 */
	@JsonProperty("visible")
	public boolean visible;
	
	@Override
	public String toString() {
		return "PluginRest [name=" + name + ", type=" + type + ", icon=" + icon + ", owner=" + owner + ", active="
				+ active + ", visible=" + visible + "]";
	}
}
