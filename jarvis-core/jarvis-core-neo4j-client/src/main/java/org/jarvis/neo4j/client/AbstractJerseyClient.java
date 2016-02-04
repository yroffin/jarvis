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
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(user, password);
		client.register(feature);

		// Mapper
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());
	}
}
