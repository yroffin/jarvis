package org.jarvis.core.resources.api.plugins;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jarvis.core.AbstractJerseyClient;
import org.jarvis.core.exception.TechnicalException;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * zway jersey client
 */
public class SlackJerseyClient extends AbstractJerseyClient {
	
	/**
	 * zway client
	 * @param url
	 * @param user
	 * @param password
	 * @param r 
	 * @param c 
	 */
	public SlackJerseyClient(String url, String user, String password, String r, String c) {
		/**
		 * initialize
		 */
		initialize(
				url,
				user,
				password,
				r,
				c);
	}

	/**
	 * call slack api
	 * @param uri 
	 * @param payload 
	 * @return String
	 */
	public PayloadBean call(String uri, PayloadBean payload) {
		/**
		 * build response
		 */
		javax.ws.rs.core.Response entity;
		try {
			entity = this.target()
			        .path(uri)
			        .request(MediaType.APPLICATION_JSON)
			        .accept(MediaType.APPLICATION_JSON)
			        .acceptEncoding("charset=UTF-8")
			        .post(Entity.entity(mapper.writeValueAsString(payload),MediaType.APPLICATION_JSON));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		
		/**
		 * verify result
		 */
		if(entity.getStatus() == 200) {
			return payload;
		} else {
			throw new TechnicalException(entity.getStatus() + ":/json");
		}
	}
}
