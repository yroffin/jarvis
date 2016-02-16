package org.jarvis.neo4j.client.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cypher result mapper
 */
public class CypherResult {
	@JsonProperty("columns")
	List<String> columns;
	@JsonProperty("data")
	List<CypherRows> data;
	/**
	 * @return List<String>
	 */
	public List<String> getColumns() {
		return columns;
	}
	/**
	 * @return List<CypherRows>
	 */
	public List<CypherRows> getData() {
		return data;
	}
	@Override
	public String toString() {
		return "CypherResult [columns=" + columns + ", data=" + data + "]";
	}
}
