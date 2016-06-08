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
package org.jarvis.neo4j.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * abstract client
 */
public class AbstractJerseyClient {

	protected String baseurl;
	protected String user;
	protected String password;
	protected Client client;
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * @param baseurl
	 * @param user 
	 * @param password 
	 */
	public AbstractJerseyClient(String baseurl, String user, String password) {
		// store Base URL
		this.baseurl = baseurl;
		this.user = user;
		this.password = password;
		
		// create HTTP Client
		this.client = ClientBuilder.newClient();
		// register auth feature if user is not null
		if(user != null) {
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(user, password);
			client.register(feature);
		}
		// Mapper
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}
}
