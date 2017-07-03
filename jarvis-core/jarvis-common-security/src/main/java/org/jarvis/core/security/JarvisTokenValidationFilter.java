package org.jarvis.core.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.client.finder.ClientFinder;
import org.pac4j.core.client.finder.DefaultClientFinder;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.sparkjava.SecurityFilter;
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
public class JarvisTokenValidationFilter extends SecurityFilter implements Filter {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * all ip to exclude from oauth2
     */
	private String[] excludes;
	private Config config;
	
	/**
	 * constructor
	 * @param config
	 * @param clients
	 * @param authorizers
	 * @param excludes
	 */
    public JarvisTokenValidationFilter(final Config config, final String clients, final String authorizers, String[] excludes) {
    	super(config, clients, authorizers);
    	this.config = config;
        this.excludes = excludes;
    }

	@Override
	public void handle(Request request, Response response) {
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

        super.handle(request, response);
	}
}
