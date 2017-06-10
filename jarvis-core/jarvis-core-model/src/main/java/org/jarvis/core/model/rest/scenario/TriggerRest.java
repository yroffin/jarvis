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

package org.jarvis.core.model.rest.scenario;

import java.util.List;

import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.device.DeviceRest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * trigger
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriggerRest extends GenericEntity {
	/**
	 * name
	 */
	@JsonProperty("name")
	public String name;
	/**
	 * icon
	 */
	@JsonProperty("icon")
	public String icon;
	/**
	 * linked devices
	 */
	@JsonProperty("devices")
	public List<DeviceRest> devices;
	/**
	 * linked scenarii
	 */
	@JsonProperty("scenarii")
	public List<ScenarioRest> scenarii;
	/**
	 * linked topic
	 */
	@JsonProperty("topic")
	public String topic;
	/**
	 * linked topic body
	 */
	@JsonProperty("body")
	public String body;
}
