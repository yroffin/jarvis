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

package org.jarvis.core.model.bean.connector;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * bean connector
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSourcePipeBean {
	/**
	 * project
	 */
	@JsonProperty("$project")
	public Map<String, Object> project;
	/**
	 * match
	 */
	@JsonProperty("$match")
	public Map<String, Object> match;
	/**
	 * sort
	 */
	@JsonProperty("$sort")
	public Map<String, Object> sort;
	/**
	 * group
	 */
	@JsonProperty("$group")
	public Map<String, Object> group;
}
