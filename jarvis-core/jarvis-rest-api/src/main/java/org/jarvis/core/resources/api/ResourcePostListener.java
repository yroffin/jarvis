package org.jarvis.core.resources.api;

import spark.Request;
import spark.Response;

/**
 * listener pattern post operation
 * @param <T> 
 */
public interface ResourcePostListener<T> {

	/**
	 * @param request
	 * @param response
	 * @param t 
	 */
	void post(Request request, Response response, T t);

	/**
	 * @param request
	 * @param response
	 * @param t 
	 */
	void put(Request request, Response response, T t);

}
