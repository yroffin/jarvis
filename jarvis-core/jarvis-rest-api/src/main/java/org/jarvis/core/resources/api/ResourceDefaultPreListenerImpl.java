package org.jarvis.core.resources.api;

import java.util.List;

import spark.Request;
import spark.Response;

/**
 * listener pattern
 * @param <T> rest class
 */
public abstract class ResourceDefaultPreListenerImpl<T> implements ResourcePreListener<T> {

	@Override
	public void post(Request request, Response response, T rest) {
	}

	@Override
	public void put(Request request, Response response, T rest) {
	}

	@Override
	public void get(Request request, Response response, T rest) {
	}

	@Override
	public void findAll(Request request, Response response, List<T> list) {
		for(T item : list) {
			get(request, response, item);
		}
	}
}
