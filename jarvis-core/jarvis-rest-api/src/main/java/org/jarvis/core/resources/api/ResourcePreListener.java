package org.jarvis.core.resources.api;

import java.util.List;

import spark.Request;
import spark.Response;

/**
 * listener pattern
 * @param <T> 
 */
public abstract class ResourcePreListener<T> {

	/**
	 * post (creation) notifier
	 * @param request
	 * @param response
	 * @param rest 
	 */
	public void post(Request request, Response response, T rest) {
		
	}

	/**
	 * put (update) notifier
	 * @param request
	 * @param response
	 * @param rest 
	 */
	public void put(Request request, Response response, T rest) {
		
	}

	/**
	 * @param request
	 * @param response
	 * @param rest 
	 */
	public void get(Request request, Response response, T rest) {
		
	}

	/**
	 * find all notifier
	 * @param request
	 * @param response
	 * @param list 
	 */
	public void findAll(Request request, Response response, List<T> list) {
		for(T item : list) {
			get(request,response, item);
		}
	}
}
