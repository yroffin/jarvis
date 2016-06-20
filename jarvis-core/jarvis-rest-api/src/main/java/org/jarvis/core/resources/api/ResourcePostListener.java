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
	 * @throws InterruptedException 
	 */
	void post(Request request, Response response, T bean) throws InterruptedException;

	/**
	 * @param request
	 * @param response
	 * @param bean 
	 */
	void put(Request request, Response response, T bean);
}
