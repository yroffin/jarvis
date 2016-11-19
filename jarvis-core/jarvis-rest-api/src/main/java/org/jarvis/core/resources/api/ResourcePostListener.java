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
	 * @param bean 
	 */
	void postBean(BEAN bean);

	/**
	 * @param bean 
	 */
	void putBean(BEAN bean);

	/**
	 * @param bean 
	 */
	public void getBean(BEAN bean);

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
