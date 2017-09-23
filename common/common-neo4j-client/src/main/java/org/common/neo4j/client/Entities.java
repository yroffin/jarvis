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
