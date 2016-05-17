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

package org.jarvis.core.model.rest.sun;

import org.jarvis.core.model.rest.GenericEntity;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * block
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SunApiRest extends GenericEntity {
	/**
	 * sunrise
	 */
	@JsonProperty("sunrise")
	public DateTime sunrise;
	/**
	 * sunset
	 */
	@JsonProperty("sunset")
	public DateTime sunset;
	/**
	 * solar_noon
	 */
	@JsonProperty("solar_noon")
	public DateTime solar_noon;
	/**
	 * day_length
	 */
	@JsonProperty("day_length")
	public DateTime day_length;
	/**
	 * civil_twilight_begin
	 */
	@JsonProperty("civil_twilight_begin")
	public DateTime civil_twilight_begin;
	/**
	 * civil_twilight_end
	 */
	@JsonProperty("civil_twilight_end")
	public DateTime civil_twilight_end;
	/**
	 * nautical_twilight_begin
	 */
	@JsonProperty("nautical_twilight_begin")
	public DateTime nautical_twilight_begin;
	/**
	 * nautical_twilight_end
	 */
	@JsonProperty("nautical_twilight_end")
	public DateTime nautical_twilight_end;
	/**
	 * astronomical_twilight_begin
	 */
	@JsonProperty("astronomical_twilight_begin")
	public DateTime astronomical_twilight_begin;
	/**
	 * astronomical_twilight_end
	 */
	@JsonProperty("astronomical_twilight_end")
	public DateTime astronomical_twilight_end;

	@Override
	public String toString() {
		return "SunApiRest [sunrise=" + sunrise + ", sunset=" + sunset + ", solar_noon=" + solar_noon + ", day_length="
				+ day_length + ", civil_twilight_begin=" + civil_twilight_begin + ", civil_twilight_end="
				+ civil_twilight_end + ", nautical_twilight_begin=" + nautical_twilight_begin
				+ ", nautical_twilight_end=" + nautical_twilight_end + ", astronomical_twilight_begin="
				+ astronomical_twilight_begin + ", astronomical_twilight_end=" + astronomical_twilight_end + "]";
	}
}
