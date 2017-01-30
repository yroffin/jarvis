package org.jarvis.core.security;

import java.util.List;

import org.pac4j.core.authorization.AuthorizationChecker;
import org.pac4j.core.authorization.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.ClientFinder;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DefaultClientFinder;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.sparkjava.SparkWebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * basic filter for token validation
 */
public class JarvisTokenValidationFilter implements Filter {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Config config;
    protected String clientName;
    protected String authorizerName;
    protected ClientFinder clientFinder = new DefaultClientFinder();
    protected AuthorizationChecker authorizationChecker = new DefaultAuthorizationChecker();

    /**
     * all ip to exclude from oauth2
     */
	private String[] excludes;

	/**
	 * @param config 
	 * @param clientName 
	 * @param authorizerName 
	 * @param excludes 
	 */
    public JarvisTokenValidationFilter(final Config config, final String clientName, final String authorizerName, String[] excludes) {
        this.config = config;
        this.clientName = clientName;
        this.authorizerName = authorizerName;
        this.excludes = excludes;
    }

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Request request, Response response) throws Exception {
		/**
		 * no protection on /api/connect and /api/oauth2
		 */
		if(request.uri().startsWith("/api/connect")) return;
		if(request.uri().startsWith("/api/oauth2")) return;

		CommonHelper.assertNotNull("config", config);
        final WebContext context = new SparkWebContext(request, response, config.getSessionStore());
        CommonHelper.assertNotNull("config.httpActionAdapter", config.getHttpActionAdapter());

        /**
         * no protection on excluded ips
         */
        for(String exclude : excludes) {
        	if(request.ip().matches(exclude)) {
                logger.warn("unprotected: {}", context.getFullRequestURL());
                response.header("Jarvis-Oauth2-Disabled", "true");
                return;
        	}
        }

        response.header("Jarvis-Oauth2-Disabled", "false");
        logger.info("url: {}", context.getFullRequestURL());

        final Clients configClients = config.getClients();
        CommonHelper.assertNotNull("configClients", configClients);
        logger.debug("clientName: {}", clientName);
        @SuppressWarnings("rawtypes")
		final List<Client> currentClients = clientFinder.find(configClients, context, this.clientName);
        logger.debug("currentClients: {}", currentClients);

        final boolean useSession = true;
        logger.debug("useSession: {}", useSession);
        final ProfileManager<UserProfile> manager = new ProfileManager<UserProfile>(context);
        UserProfile profile = manager.get(useSession);
        logger.debug("profile: {}", profile);

        // no profile and some current clients
        if (profile == null && currentClients != null && currentClients.size() > 0) {
            // loop on all clients searching direct ones to perform authentication
            for (final Client<Credentials, UserProfile> currentClient : currentClients) {
                if (currentClient instanceof DirectClient) {
                    logger.debug("Performing authentication for client: {}", currentClient);
                    final Credentials credentials;
                    try {
                        credentials = currentClient.getCredentials(context);
                        logger.debug("credentials: {}", credentials);
                    } catch (final RequiresHttpAction e) {
                        logger.warn("Authentication for client failed: {}", e);
                        Spark.halt(401);
                        throw new TechnicalException("Unexpected HTTP action", e);
                    }
                    try {
	                    profile = currentClient.getUserProfile(credentials, context);
                    } catch (final Exception e) {
                        logger.warn("Profile for client failed: {}", e);
                        Spark.halt(401);
                        throw new TechnicalException("Forbidden action", e);
                    }
                    logger.debug("profile: {}", profile);
                    if (profile != null) {
                        manager.save(useSession, profile);
                        break;
                    }
                }
            }
        }

        if (profile != null) {
            logger.debug("authorizerName: {}", authorizerName);
            if (authorizationChecker.isAuthorized(context, profile, authorizerName, config.getAuthorizers())) {
                logger.info("authenticated and authorized -> grant access to {} for {}", context.getFullRequestURL(), request.ip());
            } else {
                logger.warn("forbidden");
                forbidden(context, currentClients, profile);
            }
        } else {
            unauthorized(context, currentClients);
        }
	}
	
    @SuppressWarnings("rawtypes")
	protected void forbidden(final WebContext context, final List<Client> currentClients, final UserProfile profile) {
        config.getHttpActionAdapter().adapt(HttpConstants.FORBIDDEN, context);
    }

    @SuppressWarnings("rawtypes")
	protected void unauthorized(final WebContext context, final List<Client> currentClients) {
        config.getHttpActionAdapter().adapt(HttpConstants.UNAUTHORIZED, context);
    }
}
