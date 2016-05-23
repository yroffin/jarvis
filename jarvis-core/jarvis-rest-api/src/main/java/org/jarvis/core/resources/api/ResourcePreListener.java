package org.jarvis.core.resources.api;

import spark.Request;
import spark.Response;

/**
 * listener pattern
 * @param <T> 
 */
public interface ResourcePreListener<T> {

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void post(Request request, Response response, T rest);

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void put(Request request, Response response, T rest);

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void get(Request request, Response response, T rest);
}
