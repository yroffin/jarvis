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
package org.common.neo4j.model;

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
