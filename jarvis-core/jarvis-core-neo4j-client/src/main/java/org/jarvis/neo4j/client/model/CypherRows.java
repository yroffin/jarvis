package org.jarvis.neo4j.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cypher result mapper
 */
public class CypherRows {
	@JsonProperty("row")
	List<CypherRow> row;

	/**
	 * @return List<CypherRow>
	 */
	public List<CypherRow> getRow() {
		return row;
	}
	@Override
	public String toString() {
		return "CypherRows [row=" + row + "]";
	}
}
