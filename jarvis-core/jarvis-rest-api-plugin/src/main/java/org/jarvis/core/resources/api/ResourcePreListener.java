package org.jarvis.core.resources.api;

import java.util.List;

import spark.Request;
import spark.Response;

/**
 * default pre listener interface
 *
 * @param <T>
 */
public interface ResourcePreListener<T> {

	/**
	 * post (creation) notifier
	 * @param request
	 * @param response
	 * @param rest 
	 */
	void post(Request request, Response response, T rest);

	/**
	 * put (update) notifier
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

	/**
	 * find all notifier
	 * @param request
	 * @param response
	 * @param list 
	 */
	void findAll(Request request, Response response, List<T> list);

}