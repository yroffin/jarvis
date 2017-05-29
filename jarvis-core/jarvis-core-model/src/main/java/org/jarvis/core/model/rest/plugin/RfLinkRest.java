package org.jarvis.core.model.rest.plugin;

import org.jarvis.core.model.rest.GenericEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * zway
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RfLinkRest extends GenericEntity {
	/**
	 * command
	 */
	public String command;
}
