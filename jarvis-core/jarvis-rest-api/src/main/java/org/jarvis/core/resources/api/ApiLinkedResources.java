package org.jarvis.core.resources.api;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.common.core.type.GenericMap;

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

	/**
	 * declare api
	 * @param resource
	 * @param target
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param sortKey
	 * @param relation
	 */
	protected void declare(String resource, String target, ApiResources<T1,S1> api, ApiHrefMapper<S,S1> apiHref, String param, String sortKey, String relation) {
		get("/api/"+resource+"/:id/"+target+"", getLinks(api, apiHref, sortKey, relation));
		post("/api/"+resource+"/:id/"+target+"/"+param, postLink(api, apiHref, param, target, relation));
		put("/api/"+resource+"/:id/"+target+"/"+param, putLink(api, apiHref, param, relation));
		delete("/api/"+resource+"/:id/"+target+"/"+param, deleteLink(api, apiHref, param, relation));
	}

	/**
	 * find relation type
	 * @param request
	 * @param relation
	 * @return
	 */
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
	
	/**
	 * find all links
	 * @param api
	 * @param apiHref
	 * @param sortField
	 * @param relation
	 * @return
	 */
	protected Route getLinks(ApiResources<T1,S1> api, ApiHrefMapper<S,S1> apiHref, String sortField, String relation) {
		return new Route() {
		    @SuppressWarnings("unchecked")
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
		    		List<T1> result = new ArrayList<T1>();
		    		for(GenericEntity link : sort(apiHref.findAll(master, findRelType(request,relation)), sortField)) {
		    			result.add((T1) fromLink(link, api.doGetByIdRest(link.id)));
		    		}
			    	return writeValueAsString(result);
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
	protected Route postLink(ApiResources<T1,S1> api, ApiHrefMapper<S,S1> apiHref, String param, String href, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S1 target = api.doGetByIdBean(request.params(param));
				    	GenericEntity link = apiHref.add(master, target, new GenericMap(request.body()), href, findRelType(request,relation));
				    	return writeValueAsString(fromLink(link, api.doGetByIdRest(link.id)));
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
	 * update this link
	 * @param api
	 * @param apiHref
	 * @param param
	 * @param relation
	 * @return
	 */
	protected Route putLink(ApiResources<T1,S1> api, ApiHrefMapper<S,S1> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetByIdRest(request.params(ID));
			    	try {
			    		api.doGetByIdRest(request.params(param));
				    	GenericMap properties = apiHref.update(request.queryParams(INSTANCE), new GenericMap(request.body()));
				    	return writeValueAsString(properties);
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
	protected Route deleteLink(ApiResources<T1,S1> api, ApiHrefMapper<S,S1> apiHref, String param, String relation) {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		S master = doGetByIdBean(request.params(ID));
			    	try {
			    		S1 target = api.doGetByIdBean(request.params(param));
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

	/**
	 * mount resource
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void mount() {
		super.mount();
		for(Declare annotation : this.getClass().getAnnotationsByType(Declare.class)) {
			for(Field linkedField : this.getClass().getDeclaredFields()) {
				for(DeclareLinkedResource linkedAnnotation : linkedField.getAnnotationsByType(DeclareLinkedResource.class)) {
					for(Field hrefField : this.getClass().getDeclaredFields()) {
						for(DeclareHrefResource hrefAnnotation : hrefField.getAnnotationsByType(DeclareHrefResource.class)) {
							if(hrefAnnotation.role().equals(linkedAnnotation.role())) {
								logger.trace("Annotated link : param {} sortKey {} href {}", linkedAnnotation.param(), linkedAnnotation.sortKey(), hrefAnnotation.href());
								/**
								 * declare link
								 */
								try {
									if(linkedField.get(this) == null) {
										logger.error("Annotated link error : annotation {} linkedField {}", annotation, linkedField);
										throw new TechnicalException("Internal error while injecting resource");
									}
									declare(annotation.resource(), linkedAnnotation.role(), (ApiResources<T1,S1>) linkedField.get(this), (ApiHrefMapper<S,S1>) hrefField.get(this), linkedAnnotation.param(), linkedAnnotation.sortKey(), hrefAnnotation.href());
								} catch (IllegalArgumentException | IllegalAccessException e) {
									throw new TechnicalException(e);
								}
							}
						}
					}
				}
			}
		}
	}
}
