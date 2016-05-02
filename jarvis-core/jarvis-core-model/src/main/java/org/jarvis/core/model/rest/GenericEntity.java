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

package org.jarvis.core.model.rest;

import java.util.Map.Entry;
import java.util.Set;

import org.jarvis.core.type.GenericMap;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * simple generic entity
 */
public class GenericEntity {
	/**
	 * resource id
	 */
	@JsonProperty("id")
	public String id;
	/**
	 * timestamp
	 */
	@JsonProperty("timestamp")
	public DateTime timestamp;
	/**
	 * resource trigger
	 */
	@JsonProperty("trigger")
	public String trigger;
	/**
	 * href of this resource
	 */
	@JsonProperty("href")
	public String href;
	/**
	 * false when no security is used
	 * true in other way (if true no data backup can be done except field name in {id, key})
	 */
	@JsonProperty("isSecured")
	public boolean isSecured;
	/**
	 * instance of this resource (relationship)
	 */
	@JsonProperty("instance")
	public String instance;
	/**
	 * extended fields
	 */
	@JsonProperty("extended")
	private GenericMap extended = new GenericMap();
	
	/**
	 * has custom field
	 * @return boolean
	 */
	boolean hasExtendedProperties() {
		return extended.size() > 0;
	}

	/**
	 * put new extended field
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		extended.put(key, value);		
	}

	/**
	 * get extended field
	 * @param key
	 * @return Object
	 */
	public Object get(String key) {
		return extended.get(key);		
	}

	@Override
	public String toString() {
		return "GenericEntity [id=" + id + ", href=" + href + ", instance=" + instance + ", extended=" + extended + "]";
	}

	/**
	 * @return GenericMap
	 */
	public Set<Entry<String, Object>> get() {
		if(extended == null) {
			return new GenericMap().entrySet();
		}
		return extended.entrySet();
	}
}
