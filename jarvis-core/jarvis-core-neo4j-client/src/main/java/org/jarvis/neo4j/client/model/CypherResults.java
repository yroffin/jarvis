package org.jarvis.neo4j.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cypher result mapper
 */
public class CypherResults {
	@JsonProperty("results")
	List<CypherResult> results;
	@JsonProperty("errors")
	List<CypherError> errors;
	/**
	 * @return List<CypherResult>
	 */
	public List<CypherResult> getResults() {
		return results;
	}
	/**
	 * @return List<Object>
	 */
	public List<CypherError> getErrors() {
		return errors;
	}
	@Override
	public String toString() {
		return "CypherResults [results=" + results + ", errors=" + errors + "]";
	}
}
