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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * IOT object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IotRest extends GenericEntity {
	/**
	 * name of this iot
	 */
	@JsonProperty("name")
	public String name;
	/**
	 * owner of this iot
	 */
	@JsonProperty("owner")
	public String owner;
	/**
	 * visible
	 */
	@JsonProperty("visible")
	public boolean visible;
	/**
	 * icon of this iot
	 */
	@JsonProperty("icon")
	public String icon;
	/**
	 * tag color
	 */
	@JsonProperty("tagColor")
	public String tagColor;
	/**
	 * tag text color
	 */
	@JsonProperty("tagTextColor")
	public String tagTextColor;
}
