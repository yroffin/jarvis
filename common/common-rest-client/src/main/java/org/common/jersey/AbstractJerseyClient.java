/**
 *  Copyright 2017 Yannick Roffin
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

package org.common.jersey;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * abstract client
 */
public abstract class AbstractJerseyClient {
	protected Logger logger = LoggerFactory.getLogger(AbstractJerseyClient.class);

	protected String baseurl;
	protected String user;
	protected String password;
	protected Client client;
	protected String connect;
	protected String read;

	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * @param baseurl
	 * @param user
	 * @param password
	 */
	protected void initialize(String baseurl, String user, String password, String connect, String read) {
		// store Base URL
		this.baseurl = baseurl;
		this.user = user;
		this.password = password;
		this.connect = connect;
		this.read = read;

		// create HTTP Client
		this.client = ClientBuilder.newClient();

		// Fix timeout
		client.property(ClientProperties.CONNECT_TIMEOUT, Integer.parseInt(connect));
		client.property(ClientProperties.READ_TIMEOUT, Integer.parseInt(read));
		logger.warn("Jersey Client URL: {} Timeout Connect {} Timeout Read {}", this.baseurl, connect, read);

		// register auth feature if user is not null
		if (user != null) {
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(user, password);
			client.register(feature);
		}

		// Mapper
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	/**
	 * @return String
	 */
	public String getBaseurl() {
		return baseurl;
	}

	/**
	 * @return WebTarget
	 */
	public WebTarget target() {
		return client.target(getBaseurl());
	}
}
