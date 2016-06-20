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

package org.jarvis.core.services;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jarvis.core.AbstractJerseyClient;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.rest.sun.SunApiRest;
import org.jarvis.core.model.rest.sun.SunApiResultRest;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
public class CoreSunsetSunrise extends AbstractJerseyClient {
	
	protected static Logger logger = LoggerFactory.getLogger(CoreSunsetSunrise.class);

	@Autowired
	Environment env;

	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		/**
		 * initialize
		 */
		initialize(
				env.getProperty("jarvis.sunset.sunrise.url"),
				env.getProperty("jarvis.sunset.sunrise.user"),
				env.getProperty("jarvis.sunset.sunrise.password"),
				env.getProperty("jarvis.sunset.sunrise.timeout.connect","2000"),
				env.getProperty("jarvis.sunset.sunrise.timeout.read","2000"));
	}
	
	/**
	 * get next sunset in millis
	 * @param lat
	 * @param lng
	 * @return long
	 */
	public long getNextSunrise(String lat, String lng) {
		SunApiRest time = get(lat, lng);
		Seconds seconds = Seconds.secondsBetween(DateTime.now(), time.sunrise);
		return seconds.getSeconds() * 1000L;
	}

	/**
	 * get next sunset in millis
	 * @param lat
	 * @param lng
	 * @return long
	 */
	public long getNextSunset(String lat, String lng) {
		SunApiRest time = get(lat, lng);
		Seconds seconds = Seconds.secondsBetween(DateTime.now(), time.sunset);
		return seconds.getSeconds() * 1000L;
	}
	
	/**
	 * write this object to statistics (base on next day)
	 * @param lat 
	 * @param lng 
	 * @return String
	 */
	public SunApiRest get(String lat, String lng) {
		/**
		 * build response
		 */
		DateTime today = DateTime.now().plusDays(1);
		String d = today.getYear() + "-" + today.getMonthOfYear() + "-" + today.getDayOfMonth();
		Response entity;
		entity = client.target(baseurl)
		        .path("/json")
		        .queryParam("lat", lat)
		        .queryParam("lng", lng)
		        .queryParam("date", d)
		        .queryParam("formatted", "0")
		        .request(MediaType.APPLICATION_JSON)
		        .accept(MediaType.APPLICATION_JSON)
		        .acceptEncoding("charset=UTF-8")
		        .get();
		
		/**
		 * verify result
		 */
		if(entity.getStatus() == 200) {
			String raw = entity.readEntity(String.class);
			SunApiResultRest result = null;
			try {
				result = mapper.readValue(raw, SunApiResultRest.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			if(result == null) {
				return new SunApiRest();
			}
			return result.results;
		} else {
			throw new TechnicalException(entity.getStatus() + ":/json");
		}
	}

}
