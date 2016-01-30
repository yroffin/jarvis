package org.jarvis.core.resources.api;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.jarvis.core.type.GenericMap;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * @author kazoar
 *
 * @param <T>
 * @param <S>
 * @param <T1>
 * @param <S1> 
 */
public abstract class ApiLinkedResources<T extends GenericEntity, S extends GenericBean, T1 extends GenericEntity, S1 extends GenericBean> extends ApiResources<T, S> {

	protected void declare(String resource, String target, ApiResources<T1,S1> api, ApiHrefMapper<T,T1> apiHref, String param, String sortKey, String relation) {
		get("/api/"+resource+"/:id/"+target+"", getLinks(api, apiHref, sortKey, relation));
		post("/api/"+resource+"/:id/"+target+"/"+param, postLink(api, apiHref, param, target, relation));
		put("/api/"+resource+"/:id/"+target+"/"+param+"/:instance", putLink(api, apiHref, param, relation));
		delete("/api/"+resource+"/:id/"+target+"/"+param, deleteLink(api, apiHref, param, relation));
	}

	protected String findRelType(Request request, String relation) {
		/**
		 * href query params can filter real
		 * relation type we want to get
		 */
		String defaultRelType = relation;
		if(request.queryParams(HREF.toLowerCase()) != null) {
			defaultRelType = request.queryParams(HREF.toLowerCase());
		}
		return defaultRelType;
	}
	protected Route getLinks(ApiResources<T1,S1> api, ApiHrefMapper<T,T1> apiHref, String sortField, String relation) {
		return new Route() {
		    @SuppressWarnings("unchecked")
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
		    		List<T1> result = new ArrayList<T1>();
		    		for(GenericEntity link : sort(apiHref.findAll(master, findRelType(request,relation)), sortField)) {
		    			result.add((T1) fromLink(link, api.doGetById(link.id)));
		    		}
			    	return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		};
	}
	
	protected Route postLink(ApiResources<T1,S1> api, ApiHrefMapper<T,T1> apiHref, String param, String href, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
			    	try {
			    		T1 target = api.doGetById(request.params(param));
				    	GenericEntity link = apiHref.add(master, target, new GenericMap(request.body()), href, relation);
				    	return mapper.writeValueAsString(fromLink(link, api.doGetById(link.id)));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		};
	}
	
	protected Route putLink(ApiResources<T1,S1> api, ApiHrefMapper<T,T1> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetById(request.params(ID));
			    	try {
			    		api.doGetById(request.params(param));
				    	GenericMap properties = apiHref.update(request.params(INSTANCE), new GenericMap(request.body()));
				    	return mapper.writeValueAsString(properties);
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		};
	}
	
	protected Route deleteLink(ApiResources<T1,S1> api, ApiHrefMapper<T,T1> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
			    	try {
			    		T1 target = api.doGetById(request.params(param));
			    		apiHref.remove(master, target, request.queryParams(INSTANCE), findRelType(request,relation));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ID, response);
		    }
		};
	}
}
