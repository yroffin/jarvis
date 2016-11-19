package org.jarvis.core.resources.api;

import spark.Request;
import spark.Response;

/**
 * listener pattern post operation
 * @param <REST> 
 * @param <BEAN> 
 */
public interface ResourcePostListener<REST,BEAN> {

	/**
	 * @param request
	 * @param response
	 * @param bean 
	 */
	void postBean(Request request, Response response, BEAN bean);

	/**
	 * @param request
	 * @param response
	 * @param bean 
	 */
	void putBean(Request request, Response response, BEAN bean);

	/**
	 * @param request
	 * @param response
	 * @param bean 
	 */
	public void getBean(Request request, Response response, BEAN bean);

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void postRest(Request request, Response response, REST rest);

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void putRest(Request request, Response response, REST rest);

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	public void getRest(Request request, Response response, REST rest);
}
