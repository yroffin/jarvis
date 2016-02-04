package org.jarvis.neo4j.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * resultset
 */
public class Entities implements java.lang.AutoCloseable {

	List<Map<String, Object>> elements = new ArrayList<Map<String, Object>>();
	int cursor = 0;
	
	/**
	 * @return boolean
	 */
	public boolean hasNext() {
		return cursor < elements.size();
	}

	/**
	 * @return Map<String, Object>
	 */
	public Map<String, Object> next() {
		return elements.get(cursor++);
	}

	/**
	 * @param map
	 */
	public void add(Map<String, Object> map) {
		elements.add(map);
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public String toString() {
		return "Entities [elements=" + elements + ", cursor=" + cursor + "]";
	}
}
