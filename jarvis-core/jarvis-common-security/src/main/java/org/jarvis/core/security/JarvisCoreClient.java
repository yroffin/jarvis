package org.jarvis.core.security;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.exception.TechnicalHttpException;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;

/**
 * simple core client for pac4j
 */
public class JarvisCoreClient extends DirectClient<JarvisCoreCredentials, JarvisCoreProfile> {

	List<Oauth2ApiClient> oauth2ApiClients = new ArrayList<Oauth2ApiClient>();

	@Override
	protected JarvisCoreCredentials retrieveCredentials(WebContext context) throws HttpAction {
		JarvisCoreCredentials credentials = new JarvisCoreCredentials();
		credentials.setClientName("JarvisCoreClient");
		setAuthorizationGenerator(new JarvisAuthorization());
		return credentials;
	}

	@Override
	protected JarvisCoreProfile retrieveUserProfile(JarvisCoreCredentials credentials, WebContext context) {
		logger.info("retrieveUserProfile");

		String token = context.getRequestHeader("JarvisAuthToken");
		if(token == null) {
			logger.error("No token from {}", context.getRemoteAddr());
			throw new BadCredentialsException("No token");
		}

		/**
		 * retrieve token validation
		 */
		try {
			JarvisCoreProfile profile = null;
			/**
			 * validate token with all clients
			 */
			for(Oauth2ApiClient oauth2ApiClient : oauth2ApiClients) {
				if(profile != null) continue;
				profile = oauth2ApiClient.getAccessTokenInfo(token);
			}
			if(profile == null) {
				logger.error("No token valid {} from {}", token, context.getRemoteAddr());
				throw new BadCredentialsException("No token type");
			}
			logger.info("retrieveUserProfile profile={}", profile);
			return profile;
		} catch (TechnicalHttpException e) {
			logger.error("Token is not valid {}", e);
			throw new BadCredentialsException("Token is not valid", e);
		} catch (Exception e) {
			logger.error("Token is not valid {}", e);
			throw new BadCredentialsException("Token is not valid", e);
		}
	}

	@Override
	protected void internalInit(WebContext context) {
		/**
		 * define google api client to retrieve token
		 * on each call
		 */
		oauth2ApiClients.add(new Oauth2ApiClient("https://www.googleapis.com/oauth2", "access_token", "/v3/tokeninfo"));
		oauth2ApiClients.add(new Oauth2ApiClient("https://graph.facebook.com", "access_token", "/me"));
	}
}
