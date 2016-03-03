/**
 *  Copyright 2015 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jarvis.core.resources.api;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.services.ApiService;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

/**
 * RESOURCE api
 *
 * @param <T>
 * @param <S>
 */
public abstract class ApiResources<T extends GenericEntity,S extends GenericBean> extends ApiMapper {
	protected Logger logger = LoggerFactory.getLogger(ApiResources.class);

	private List<ResourcePreListener<T>> preListeners = new ArrayList<ResourcePreListener<T>>();
	private List<ResourcePostListener<S>> postListeners = new ArrayList<ResourcePostListener<S>>();
	
	/**
	 * add new listener
	 * @param listener
	 */
	public void addListener(ResourcePreListener<T> listener) {
		preListeners.add(listener);
    }

	/**
	 * add new listener
	 * @param listener
	 */
	public void addListener(ResourcePostListener<S> listener) {
		postListeners.add(listener);
    }

	private void notifyPost(Request request, Response response, S s) {
        for(ResourcePostListener<S> listener : postListeners){
            listener.post(request,response,s);
        }
	}

	private void notifyPrePost(Request request, Response response, T t) {
        for(ResourcePreListener<T> listener : preListeners){
            listener.post(request,response,t);
        }
	}

	private void notifyPut(Request request, Response response, S s) {
		/**
		 * notify to all listener on server side
		 */
        for(ResourcePostListener<S> listener : postListeners){
            listener.put(request,response,s);
        }
	}

	private void notifyPrePut(Request request, Response response, T t) {
        for(ResourcePreListener<T> listener : preListeners){
            listener.put(request,response,t);
        }
	}

	@Autowired
	Environment env;
	
	@Autowired
	protected ApiNeo4Service apiNeo4Service;

	/**
	 * internal api service
	 */
	ApiService<S> apiService = new ApiService<S>();

	/**
	 * bean class
	 */
	private Class<S> beanClass;

	/**
	 * rest class
	 */
	private Class<T> restClass;

	/**
	 * spring init
	 */
	@PostConstruct
	protected
	void init() {
		super.init();
		apiService.setApiNeo4Service(apiNeo4Service);
		apiService.setBeanClass(beanClass);
	}

	/**
	 * set bean class
	 * @param klass
	 */
	protected void setBeanClass(Class<S> klass) {
		beanClass = klass;
	}

	/**
	 * set rest class
	 * @param klass
	 */
	protected void setRestClass(Class<T> klass) {
		restClass = klass;		
	}

	protected void declare(String resource) {
		get("/api/"+resource+"", getResources());
		get("/api/"+resource+"/:id", getResource());
		post("/api/"+resource+"", postResource());
		post("/api/"+resource+"/:id", taskResource());
		put("/api/"+resource+"/:id", putResource());
		delete("/api/"+resource+"/:id", deleteResource());
	}

	protected Route getResources() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		};
	}
		
	protected Route getResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ID, response);
		    }
		};
	}

	protected Route postResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, restClass);
		    }
		};
	}

	protected Route taskResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doTask(request, ID, TASK, response, restClass);
		    }
		};
	}

	protected Route putResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ID, response, restClass);
		    }
		};
	}

	protected Route deleteResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ID, response, restClass);
		    }
		};
	}

	/**
	 * find all elements
	 * @return List<Rest>
	 */
	public List<T> doFindAll() {
		List<T> result = new ArrayList<T>();
		for(S item : apiService.findAll()) {
			result.add(mapperFactory.getMapperFacade().map(item, restClass));
		}
		return result;
	}

	/**
	 * find element by id
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException 
	 */
	public S doGetByIdBean(String id) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(apiService.getById(id), beanClass);
	}
	
	/**
	 * find element by id
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException 
	 */
	public T doGetById(String id) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(apiService.getById(id), restClass);
	}

	/**
	 * create new entity
	 * @param r 
	 * @return Rest
	 */
	public T doCreate(T r) {
		S bean = mapperFactory.getMapperFacade().map(r, beanClass);
		return mapperFactory.getMapperFacade().map(apiService.create(bean), restClass);
	}
	
	/**
	 * delete by id
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public T doDelete(String id) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(
				apiService.remove(id),
				restClass);
	}

	/**
	 * update entity
	 * @param id
	 * @param r 
	 * @return Rest
	 * @throws TechnicalNotFoundException 
	 */
	public T doUpdate(String id, T r) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(
				apiService.update(id, mapperFactory.getMapperFacade().map(r, beanClass)), 
				restClass);
	}

	/**
	 * find all elements
	 * @param request
	 * @param response
	 * @return String
	 * @throws Exception
	 */
	public String doFindAll(Request request, Response response) throws Exception {
    	return mapper.writeValueAsString(doFindAll());
    }

	/**
	 * get element by id
	 * @param request
	 * @param id
	 * @param response
	 * @return String
	 * @throws Exception
	 */
	public String doGetById(Request request, String id, Response response) throws Exception {
    	try {
    		return mapper.writeValueAsString(doGetById(request.params(id)));
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
    	}
    }

	/**
	 * create entity
	 * @param request
	 * @param response
	 * @param klass
	 * @return String
	 * @throws Exception
	 */
	public String doCreate(Request request, Response response, Class<T> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		T k = mapper.readValue(request.body(), klass);
		notifyPrePost(request,response,k);
		T r = doCreate(k);
		notifyPost(request,response,mapperFactory.getMapperFacade().map(r, beanClass));
    	return mapper.writeValueAsString(r);
    }

	/**
	 * update entity
	 * @param request
	 * @param id
	 * @param response
	 * @param klass
	 * @return String
	 * @throws Exception
	 */
	public String doUpdate(Request request, String id, Response response, Class<T> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
			T k = mapper.readValue(request.body(), klass);
			notifyPrePut(request,response,k);
			T r = doUpdate(request.params(id), k);
			notifyPut(request,response,mapperFactory.getMapperFacade().map(r, beanClass));
	    	return mapper.writeValueAsString(r);
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
    	}
    }

	/**
	 * delete node
	 * @param request
	 * @param id 
	 * @param response
	 * @param klass
	 * @return String
	 * @throws Exception
	 */
	public String doDelete(Request request, String id, Response response, Class<T> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
        	return mapper.writeValueAsString(doDelete(request.params(id)));
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
    	}
    }

	/**
	 * execute real task on this resource, all task must be overridden in each
	 * resources
	 * 
	 * @param bean
	 * @param args
	 * @param taskType
	 * @return String
	 * @throws Exception
	 */
	public abstract ResourcePair doRealTask(S bean, GenericMap args, TaskType taskType) throws Exception;

	/**
	 * create entity
	 * @param request
	 * @param id 
	 * @param task 
	 * @param response
	 * @param klass
	 * @return String
	 * @throws TechnicalNotFoundException 
	 */
	public String doTask(Request request, String id, String task, Response response, Class<T> klass) throws TechnicalNotFoundException {
		GenericMap body = null;
    	if(request.contentType() != null && request.contentType().startsWith("multipart/form-data")) {
    		body = new GenericMap();
    		body.put("multipart/form-data", extractMultipart(request,response));
    	} else {
	    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
	    		response.status(403);
	    		return "";
	    	}
	    	try {
				body = (GenericMap) mapper.readValue(request.body(),GenericMap.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
    	}
    	try {
    		Object result = doExecute(
    				response,
    				request.params(id),
    				body,
    				TaskType.valueOf(request.queryParams(task).toUpperCase()));
    		/**
    		 * task can return String or Object
    		 * TODO
    		 * return List
    		 */
    		if(GenericMap.class == result.getClass()) {
    			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
    		} else {
    			return (String) result;
    		}
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
    	} catch (JsonParseException e) {
			throw new TechnicalException(e);
		} catch (JsonMappingException e) {
			throw new TechnicalException(e);
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
    }

	private String extractMultipart(Request request, Response response) {
		String location = "/tmp";  // the directory location where files will be stored
		long maxFileSize = 100000000;  // the maximum size allowed for uploaded files
		long maxRequestSize = 100000000;  // the maximum size allowed for multipart/form-data requests
		int fileSizeThreshold = 1024;  // the size threshold after which files will be written to disk
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
		request.raw().setAttribute("org.eclipse.multipartConfig", multipartConfigElement);
		try {
			 // file is name of the upload form
			Part file = request.raw().getPart("file");
			try (final InputStream in = file.getInputStream()) {
				StringWriter writer = new StringWriter();
				IOUtils.copy(in, writer);
				file.delete();
				return writer.toString();
			}
		} catch (IOException | ServletException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @param response 
	 * @param id
	 * @param body 
	 * @param taskType
	 * @return GenericMap
	 * @throws TechnicalNotFoundException
	 */
	public Object doExecute(Response response, String id, GenericMap body, TaskType taskType) throws TechnicalNotFoundException {
		/**
		 * read object by id
		 */
		S bean = null;
		if(!id.equals("*")) {
			bean = apiService.getById(id);
			try {
				logger.info("SCRIPT - CONTEXT {} {}", beanClass.getName(), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean));
			} catch (JsonProcessingException e) {
				throw new TechnicalException(e);
			}
		} else {
			logger.info("SCRIPT - CONTEXT {}", "*");
		}
		try {
			logger.info("SCRIPT - INPUT   {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		ResourcePair result = null;
		try {
			result = doRealTask(bean, body, taskType);
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
		logger.info("SCRIPT - OUTPUT {}", result);
		try {
			switch(result.getKey()) {
				case OBJECT:
					return (GenericMap) mapper.readValue(result.getValue(), GenericMap.class);
				case FILE_STREAM:
					response.type("application/octet-stream");
					return result.getValue();
				default:
					return result.getValue();
			}
		} catch (IOException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * @param list
	 * @param field 
	 * @return List<CommandRest>
	 */
	public List<GenericEntity> sort(List<GenericEntity> list, String field) {
		/**
		 * sort by order
		 */
		Collections.sort(list, new Comparator<GenericEntity>() {
	
			@Override
			public int compare(GenericEntity l, GenericEntity r) {
				String left = (String) l.get(field);
				if(left == null) {
					return -1;
				}
				String right = (String) r.get(field);
				if(right != null) {
					return left.compareTo(right);
				} else {
					return 1;
				}
			}
			
		});
		return list;
	}
	/**
	 * build script with relationship
	 * @param link
	 * @return ScriptPluginRest
	 * @throws TechnicalNotFoundException 
	 */
	protected GenericEntity fromLink(GenericEntity link, GenericEntity rest) throws TechnicalNotFoundException {
		rest.instance = link.instance;
		for(Entry<String, Object> property : link.get()) {
			rest.put(property.getKey(), property.getValue());
		}
		return rest;
	}
}
