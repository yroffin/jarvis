package org.jarvis.neo4j.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cypher result mapper
 */
public class CypherError {
	@JsonProperty("code")
	String code;
	@JsonProperty("message")
	String message;
	/**
	 * @return String
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @return String
	 */
	public String getMessage() {
		return message;
	}
}
