package org.jarvis.core.security;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;

/**
 * authorization
 */
public class JarvisAuthorization implements AuthorizationGenerator<JarvisCoreProfile> {

	@Override
	public JarvisCoreProfile generate(WebContext context, JarvisCoreProfile profile) {
		return profile;
	}

}
