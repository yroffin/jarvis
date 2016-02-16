package org.jarvis.neo4j.client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * cypher result mapper
 */
public class CypherRow {
	Integer integer;
	Map<String,Object> object;
	CypherRow(Integer value) {
		integer = value;
	}
	@JsonCreator
	CypherRow(Map<String,Object> value) {
		object = value;
	}
	/**
	 * @return boolean
	 */
	public boolean isInteger() {
		return integer != null;
	}
	/**
	 * @return boolean
	 */
	public boolean isObject() {
		return object != null;
	}
	/**
	 * @return Integer
	 */
	public Integer getInteger() {
		return integer;
	}
	/**
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getObject() {
		return object;
	}
	@Override
	public String toString() {
		return "CypherRow [integer=" + integer + ", object=" + object + "]";
	}
}
