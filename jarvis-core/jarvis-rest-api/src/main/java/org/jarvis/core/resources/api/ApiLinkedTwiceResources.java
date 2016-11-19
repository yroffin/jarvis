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
 * declare second level HREF API
 *
 * @param <T>
 * @param <S>
 * @param <T1>
 * @param <S1> 
 * @param <T2> 
 * @param <S2> 
 */
public abstract class ApiLinkedTwiceResources<T extends GenericEntity, S extends GenericBean, T1 extends GenericEntity, S1 extends GenericBean, T2 extends GenericEntity, S2 extends GenericBean> extends ApiLinkedResources<T, S, T1, S1> {

	/**
	 * declare second level HREF API
	 * @param resource
	 * @param target
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param sortKey
	 * @param relation
	 */
	protected void declareSecond(String resource, String target, ApiResources<T2,S2> api, ApiHrefMapper<S,S2> apiHref, String param, String sortKey, String relation) {
		get("/api/"+resource+"/:id/"+target+"", getSecondLinks(api, apiHref, sortKey, relation));
		post("/api/"+resource+"/:id/"+target+"/"+param, postSecondLink(api, apiHref, param, target, relation));
		put("/api/"+resource+"/:id/"+target+"/"+param+"/:instance", putSecondLink(api, apiHref, param, relation));
		delete("/api/"+resource+"/:id/"+target+"/"+param, deleteSecondLink(api, apiHref, param, relation));
	}

	/**
	 * find all links
	 * @param api
	 * @param apiHref
	 * @param sortField
	 * @param relation
	 * @return
	 */
	protected Route getSecondLinks(ApiResources<T2,S2> api, ApiHrefMapper<S,S2> apiHref, String sortField, String relation) {
		return new Route() {
		    @SuppressWarnings("unchecked")
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
		    		List<T2> result = new ArrayList<T2>();
		    		for(GenericEntity link : sort(apiHref.findAll(master, findRelType(request,relation)), sortField)) {
		    			result.add((T2) fromLink(link, api.doGetByIdRest(link.id)));
		    		}
			    	return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		};
	}
	
	/**
	 * create link
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param href
	 * @param relation
	 * @return
	 */
	protected Route postSecondLink(ApiResources<T2,S2> api, ApiHrefMapper<S,S2> apiHref, String param, String href, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S2 target = api.doGetByIdBean(request.params(param));
				    	GenericEntity link = apiHref.add(master, target, new GenericMap(request.body()), href, relation);
				    	return mapper.writeValueAsString(fromLink(link, api.doGetByIdRest(link.id)));
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
	
	/**
	 * update link
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param relation
	 * @return
	 */
	protected Route putSecondLink(ApiResources<T2,S2> api, ApiHrefMapper<S,S2> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetByIdRest(request.params(ID));
			    	try {
			    		api.doGetByIdRest(request.params(param));
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
	
	/**
	 * delete link
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param relation
	 * @return
	 */
	protected Route deleteSecondLink(ApiResources<T2,S2> api, ApiHrefMapper<S,S2> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S2 target = api.doGetByIdBean(request.params(param));
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
