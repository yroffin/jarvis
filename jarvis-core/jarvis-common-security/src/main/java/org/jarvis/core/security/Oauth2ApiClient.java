package org.jarvis.core.security;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalHttpException;
import org.common.jersey.AbstractJerseyClient;

/**
 * simple oauth2 api client
 */
public class Oauth2ApiClient extends AbstractJerseyClient {

	private String token;
	private String path;

	/**
	 * constructor
	 * @param baseurl 
	 * @param token 
	 * @param path 
	 */
	public Oauth2ApiClient(String baseurl, String token, String path) {
		/**
		 * initialize
		 */
		initialize(
				baseurl,
				null,
				null,
				"2000",
				"2000");

		this.token = token;
		this.path = path;
	}

	/**
	 * @param tokenKey 
	 * @return CommonProfile
	 * @throws TechnicalHttpException
	 */
	public JarvisCoreProfile getAccessTokenInfo(String tokenKey) throws TechnicalHttpException {
		Response entity;
		entity = client.target(baseurl)
				.queryParam(token, tokenKey)
		        .path(path)
		        .request(MediaType.APPLICATION_JSON)
		        .accept(MediaType.APPLICATION_JSON)
		        .acceptEncoding("charset=UTF-8")
		        .get();
		
		if(entity.getStatus() == 200) {
			Map<?, ?> body = null;
			try {
				body = mapper.readValue(entity.readEntity(String.class), Map.class);
				/**
				 * google case
				 */
				if(body.get("email_verified") != null && body.get("email_verified").equals("true")) {
					JarvisCoreProfile profile = new JarvisCoreProfile();
					profile.addAttribute("email", body.get("email"));
					profile.addAttribute("token", tokenKey);
					return profile;
				}
				/**
				 * facebook case
				 */
				if(body.get("name") != null) {
					JarvisCoreProfile profile = new JarvisCoreProfile();
					profile.addAttribute("name", body.get("name"));
					profile.addAttribute("token", tokenKey);
					return profile;
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			return null;
		} else {
			throw new TechnicalHttpException(entity.getStatus(), "While retrieving token info");
		}
	}
}
