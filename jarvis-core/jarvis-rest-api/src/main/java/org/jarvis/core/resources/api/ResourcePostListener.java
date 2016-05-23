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
	 * @param bean 
	 */
	void post(Request request, Response response, T bean);

	/**
	 * @param request
	 * @param response
	 * @param bean 
	 */
	void put(Request request, Response response, T bean);
}
