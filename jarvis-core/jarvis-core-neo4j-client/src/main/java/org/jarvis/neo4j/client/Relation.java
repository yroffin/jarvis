package org.jarvis.neo4j.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Node
 */
public class Relation {

	String id;
	String from;
	String to;
	Map<String, Object> fields = new LinkedHashMap<String, Object>();
	
	/**
	 * @param id
	 */
	public Relation(String id) {
		this.id = id;
	}

	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	
	/**
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @param to
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @param map
	 */
	public Relation(Map<String, Object> map) {
		for(Entry<String, Object> entry : map.entrySet()) {
			fields.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param name
	 * @param str
	 */
	public void setProperty(String name, String str) {
		fields.put(name, str);		
	}

	/**
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		fields.put(name, value);		
	}

	/**
	 * @param name
	 */
	public void removeProperty(String name) {
		fields.remove(name);
	}

	/**
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getAllProperties() {
		return fields;
	}

	/**
	 * @param key 
	 * @return DateTime
	 */
	public Object getProperty(String key) {
		return fields.get(key);
	}

	/**
	 * @return String
	 */
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", fields=" + fields + "]";
	}
}
