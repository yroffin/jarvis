package org.jarvis.core.security;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.exception.TechnicalHttpException;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;

/**
 * simple core client for pac4j
 */
public class JarvisCoreClient extends DirectClient<JarvisCoreCredentials, JarvisCoreProfile> {

	List<Oauth2ApiClient> oauth2ApiClients = new ArrayList<Oauth2ApiClient>();

	/**
	 * constructor
	 */
	public JarvisCoreClient() {
		/**
		 * credential extractor
		 */
		this.setCredentialsExtractor(new CredentialsExtractor<JarvisCoreCredentials>() {
			@Override
			public JarvisCoreCredentials extract(WebContext context) throws HttpAction, CredentialsException {
				JarvisCoreCredentials credentials = new JarvisCoreCredentials();
				credentials.setClientName("JarvisCoreClient");
				return credentials;
			}

		});
		/**
		 * authenticator
		 */
		this.setAuthenticator(new Authenticator<JarvisCoreCredentials>() {
			@Override
			public void validate(JarvisCoreCredentials credentials, WebContext context)
					throws HttpAction, CredentialsException {
				logger.info("validate {} {}", credentials, context);

				String token = context.getRequestHeader("JarvisAuthToken");
				if (token == null) {
					logger.error("No token from {}", context.getRemoteAddr());
					throw new CredentialsException("No token");
				}

				/**
				 * retrieve token validation
				 */
				try {
					JarvisCoreProfile profile = null;
					/**
					 * validate token with all clients
					 */
					for (Oauth2ApiClient oauth2ApiClient : oauth2ApiClients) {
						if (profile != null)
							continue;
						profile = oauth2ApiClient.getAccessTokenInfo(token);
					}
					if (profile == null) {
						logger.error("No token valid {} from {}", token, context.getRemoteAddr());
						throw new CredentialsException("No token type");
					}
					logger.info("retrieveUserProfile profile={}", profile);
					credentials.setUserProfile(profile);
				} catch (TechnicalHttpException e) {
					logger.error("Token is not valid {}", e);
					throw new CredentialsException("Token is not valid", e);
				} catch (Exception e) {
					logger.error("Token is not valid {}", e);
					throw new CredentialsException("Token is not valid", e);
				}
			}

		});
		/**
		 * and generator
		 */
		setAuthorizationGenerator(new JarvisAuthorization());
	}

	@Override
	protected void clientInit(WebContext context) {
		/**
		 * define google api client to retrieve token on each call
		 */
		oauth2ApiClients.add(new Oauth2ApiClient("https://www.googleapis.com/oauth2", "access_token", "/v3/tokeninfo"));
		oauth2ApiClients.add(new Oauth2ApiClient("https://graph.facebook.com", "access_token", "/me"));
	}
}
