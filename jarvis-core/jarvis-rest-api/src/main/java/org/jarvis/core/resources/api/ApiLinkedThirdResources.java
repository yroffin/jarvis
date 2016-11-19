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
 * declare third level HREF API
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

	protected void declareThird(String resource, String target, ApiResources<T3,S3> api, ApiHrefMapper<S,S3> apiHref, String param, String sortKey, String relation) {
		get("/api/"+resource+"/:id/"+target+"", getThirdLinks(api, apiHref, sortKey, relation));
		post("/api/"+resource+"/:id/"+target+"/"+param, postThirdLink(api, apiHref, param, target, relation));
		put("/api/"+resource+"/:id/"+target+"/"+param+"/:instance", putThirdLink(api, apiHref, param, relation));
		delete("/api/"+resource+"/:id/"+target+"/"+param, deleteThirdLink(api, apiHref, param, relation));
	}

	/**
	 * find all links
	 * @param api
	 * @param apiHref
	 * @param sortField
	 * @param relation
	 * @return
	 */
	protected Route getThirdLinks(ApiResources<T3,S3> api, ApiHrefMapper<S,S3> apiHref, String sortField, String relation) {
		return new Route() {
		    @SuppressWarnings("unchecked")
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
		    		List<T3> result = new ArrayList<T3>();
		    		for(GenericEntity link : sort(apiHref.findAll(master, findRelType(request,findRelType(request,relation))), sortField)) {
		    			result.add((T3) fromLink(link, api.doGetByIdRest(link.id)));
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
	protected Route postThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<S,S3> apiHref, String param, String href, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S3 target = api.doGetByIdBean(request.params(param));
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
	protected Route putThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<S,S3> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetByIdBean(request.params(ID));
			    	try {
			    		api.doGetByIdBean(request.params(param));
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
	 * delete this link
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param relation
	 * @return
	 */
	protected Route deleteThirdLink(ApiResources<T3,S3> api, ApiHrefMapper<S,S3> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S3 target = api.doGetByIdBean(request.params(param));
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
