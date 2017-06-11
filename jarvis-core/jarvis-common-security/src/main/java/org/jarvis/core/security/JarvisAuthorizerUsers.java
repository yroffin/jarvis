package org.jarvis.core.security;

import java.util.List;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;

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
	public boolean isAuthorized(WebContext context, List<JarvisCoreProfile> profiles) throws HttpAction {
		for(String user : users) {
			for(JarvisCoreProfile profile: profiles) {
				if(user.equals(profile.getEmail())) {
					return true;
				}
			}
		}
		return false;
	}

}
