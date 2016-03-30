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