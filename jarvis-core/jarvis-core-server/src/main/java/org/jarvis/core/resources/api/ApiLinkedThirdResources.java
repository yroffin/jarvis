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
 * @param <T2> 
 * @param <S2> 
 * @param <T3> 
 * @param <S3> 
 */
public abstract class ApiLinkedThirdResources<T extends GenericEntity, S extends GenericBean, T1 extends GenericEntity, S1 extends GenericBean, T2 extends GenericEntity, S2 extends GenericBean, T3 extends GenericEntity, S3 extends GenericBean> extends ApiLinkedTwiceResources<T, S, T1, S1, T2, S2> {

	protected void declareThird(String resource, String target, ApiResources<T3,S3> api, ApiHrefMapper<T,T3> apiHref, String param, String sortKey) {
		get("/api/"+resource+"/:id/"+target+"", getThirdLinks(api, apiHref, sortKey));
		post("/api/"+resource+"/:id/"+target+"/"+param, postThirdLink(api, apiHref, param, target));
		put("/api/"+resource+"/:id/"+target+"/"+param+"/:instance", putThirdLink(api, apiHref, param));
		delete("/api/"+resource+"/:id/"+target+"/"+param, deleteThirdLink(api, apiHref, param));
	}

	protected Route getThirdLinks(ApiResources<T3,S3> api, ApiHrefMapper<T,T3> apiHref, String sortField) {
		return new Route() {
		    @SuppressWarnings("unchecked")
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
		    		List<T3> result = new ArrayList<T3>();
		    		for(GenericEntity link : sort(apiHref.findAll(master), sortField)) {
		    			result.add((T3) fromLink(link, api.doGetById(link.id)));
		    		}
			    	return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		};
	}
	
	protected Route postThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<T,T3> apiHref, String param, String href) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
			    	try {
			    		T3 target = api.doGetById(request.params(param));
				    	GenericEntity link = apiHref.add(master, target, new GenericMap(request.body()), href);
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
	
	protected Route putThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<T,T3> apiHref, String param) {
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
	
	protected Route deleteThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<T,T3> apiHref, String param) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		T master = doGetById(request.params(ID));
			    	try {
			    		T3 target = api.doGetById(request.params(param));
			    		apiHref.remove(master, target, request.queryParams(INSTANCE));
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
