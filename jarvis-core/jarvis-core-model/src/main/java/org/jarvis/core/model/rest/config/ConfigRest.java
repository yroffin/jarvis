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

package org.jarvis.core.model.rest.config;

import org.jarvis.core.model.rest.GenericEntity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * VIEW object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigRest extends GenericEntity {
	/**
	 * name
	 */
	@JsonProperty("name")
	public String name;
	/**
	 * active
	 */
	@JsonProperty("active")
	public boolean active;
	/**
	 * opacity
	 */
	@JsonProperty("opacity")
	public String opacity;
	/**
	 * backgroundUrl
	 */
	@JsonProperty("backgroundUrl")
	public String backgroundUrl;
	/**
	 * tilesXs
	 */
	@JsonProperty("tilesXs")
	public String tilesXs;
	/**
	 * tilesGtXs
	 */
	@JsonProperty("tilesGtXs")
	public String tilesGtXs;
	/**
	 * tilesSm
	 */
	@JsonProperty("tilesSm")
	public String tilesSm;
	/**
	 * tilesGtSm
	 */
	@JsonProperty("tilesGtSm")
	public String tilesGtSm;
	/**
	 * tilesMd
	 */
	@JsonProperty("tilesMd")
	public String tilesMd;
	/**
	 * tilesGtMd
	 */
	@JsonProperty("tilesGtMd")
	public String tilesGtMd;
	/**
	 * tilesLg
	 */
	@JsonProperty("tilesLg")
	public String tilesLg;
	/**
	 * tilesGtLg
	 */
	@JsonProperty("tilesGtLg")
	public String tilesGtLg;
	/**
	 * tilesXl
	 */
	@JsonProperty("tilesXl")
	public String tilesXl;
	/**
	 * tilesXl
	 */
	@JsonProperty("system")
	public ConfigSystemRest system;
	
	@Override
	public String toString() {
		return "ConfigRest [name=" + name + ", active=" + active + ", opacity=" + opacity + ", backgroundUrl="
				+ backgroundUrl + ", tilesXs=" + tilesXs + ", tilesGtXs=" + tilesGtXs + ", tilesSm=" + tilesSm
				+ ", tilesGtSm=" + tilesGtSm + ", tilesMd=" + tilesMd + ", tilesGtMd=" + tilesGtMd + ", tilesLg="
				+ tilesLg + ", tilesGtLg=" + tilesGtLg + ", tilesXl=" + tilesXl + "]";
	}
}
