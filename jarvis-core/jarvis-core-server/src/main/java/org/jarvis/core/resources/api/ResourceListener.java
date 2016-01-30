package org.jarvis.core.resources.api;

import spark.Request;
import spark.Response;

/**
 * listener pattern
 * @param <T> 
 */
public interface ResourceListener<T> {

	/**
	 * @param request
	 * @param response
	 * @param result
	 */
	void post(Request request, Response response, T result);

}
