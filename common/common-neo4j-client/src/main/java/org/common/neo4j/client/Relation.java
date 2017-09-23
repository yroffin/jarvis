/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.common.neo4j.client;

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
