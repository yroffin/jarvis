package org.jarvis.core.log4j2;

import org.jarvis.core.AbstractJerseyClient;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * simple gelf client
 */
public class GelfRestClient extends AbstractJerseyClient {
	/**
	 * @param url
	 */
	public GelfRestClient(String url) {
		/**
		 * initialize
		 */
		initialize(
				url,
				null,
				null,
				"2000",
				"2000");
	}

	/**
	 * @return ObjectMapper
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}
}
