package org.jarvis.core.resources.api;

import spark.Request;
import spark.Response;

/**
 * default abstract class
 * @param <REST> 
 * @param <BEAN> bean class
 */
public abstract class ResourceDefaultPostListenerImpl<REST,BEAN> implements ResourcePostListener<REST,BEAN> {

	@Override
	public void postBean(Request request, Response response, BEAN bean) {
	}

	@Override
	public void putBean(Request request, Response response, BEAN bean) {
	}

	@Override
	public void getBean(Request request, Response response, BEAN bean) {
	}

	@Override
	public void postRest(Request request, Response response, REST rest) {
	}

	@Override
	public void putRest(Request request, Response response, REST rest) {
	}

	@Override
	public void getRest(Request request, Response response, REST rest) {
	}
}
