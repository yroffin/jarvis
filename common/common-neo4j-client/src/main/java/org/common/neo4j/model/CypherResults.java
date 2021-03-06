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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * cypher result mapper
 */
public class CypherResults {
	@JsonProperty("results")
	List<CypherResult> results;
	@JsonProperty("errors")
	List<CypherError> errors;
	/**
	 * @return List<CypherResult>
	 */
	public List<CypherResult> getResults() {
		return results;
	}
	/**
	 * @return List<Object>
	 */
	public List<CypherError> getErrors() {
		return errors;
	}
	@Override
	public String toString() {
		return "CypherResults [results=" + results + ", errors=" + errors + "]";
	}
}
