package org.jarvis.core.security;

import org.pac4j.core.credentials.Credentials;

/**
 * default credentials
 */
public class JarvisCoreCredentials extends Credentials {

	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void clear() {
	}

	@Override
	public String toString() {
		return "JarvisCoreCredentials [getClientName()=" + getClientName() + "]";
	}
}
