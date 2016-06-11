package org.jarvis.core.security;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.WebContext;

/**
 * check with user list
 */
public class JarvisAuthorizerUsers implements Authorizer<JarvisCoreProfile> {

	private String[] users;

	/**
	 * @param users
	 */
	public JarvisAuthorizerUsers(String[] users) {
		this.users = users;
	}

	@Override
	public boolean isAuthorized(WebContext context, JarvisCoreProfile profile) {
		for(String user : users) {
			if(user.equals(profile.getEmail())) {
				return true;
			}
		}
		return false;
	}

}
