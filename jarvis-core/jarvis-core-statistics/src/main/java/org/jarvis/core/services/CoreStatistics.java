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

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.rest.GenericEntity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * main daemon
 */
@Component
public class CoreStatistics {
	
	protected static Logger logger = LoggerFactory.getLogger(CoreStatistics.class);
	protected ObjectMapper mapper = new ObjectMapper();

	@Autowired
	Environment env;

	protected String baseurl;
	protected String user;
	protected String password;
	protected Client client;
	
	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		// store Base URL
		this.baseurl = env.getProperty("jarvis.elasticsearch.url");
		this.user = env.getProperty("jarvis.elasticsearch.user");
		this.password = env.getProperty("jarvis.elasticsearch.password");
		
		// create HTTP Client
		this.client = ClientBuilder.newClient();
		if(user != null) {
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(user, password);
			client.register(feature);
		}
		
		// Mapper
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	/**
	 * write this object to statistics
	 * @param value
	 * @param index
	 * @param basename
	 * @return String
	 */
	public String write(GenericEntity value, String index, String basename) {
		if(value.timestamp == null) {
			value.timestamp = new DateTime();
		}
		
		/**
		 * build response
		 */
		Response entity;
		try {
			entity = client.target(baseurl)
			        .path(index + "/" + basename)
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .post(Entity.entity(mapper.writeValueAsString(value),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		try {
			System.err.println(mapper.writeValueAsString(value));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * verify result
		 */
		if(entity.getStatus() == 201) {
			String result = entity.readEntity(String.class);
			return result;
		} else {
			throw new TechnicalException(entity.getStatus() + ":" + index + "/" + value.getClass().getSimpleName());
		}
	}

}
