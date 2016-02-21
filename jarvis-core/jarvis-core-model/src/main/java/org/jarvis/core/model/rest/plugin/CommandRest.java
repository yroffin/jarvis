package org.jarvis.core.model.rest.plugin;

import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.type.CommandType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * commande
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommandRest extends GenericEntity {
	/**
	 * name
	 */
	@JsonProperty("name")
	public String name;
	/**
	 * type
	 */
	@JsonProperty("type")
	public CommandType type;
	/**
	 * icon
	 */
	@JsonProperty("icon")
	public String icon;
	/**
	 * mode
	 */
	@JsonProperty("mode")
	public String mode;
	/**
	 * body
	 */
	@JsonProperty("body")
	public String body;
}
