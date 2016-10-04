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
import org.jarvis.core.services.ApiService;
import org.jarvis.core.services.CoreStatistics;
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
public abstract class ApiResources<T extends GenericEntity,S extends GenericBean> extends ApiGenericResources implements ApiDefaultResources {
	protected Logger logger = LoggerFactory.getLogger(ApiResources.class);

	private List<ResourcePreListener<T>> preListeners = new ArrayList<>();
	private List<ResourcePostListener<S>> postListeners = new ArrayList<>();
	
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

	/**
	 * notifier
	 * @param request
	 * @param response
	 * @param r
	 */
	private void notifyFindAll(Request request, Response response, List<T> r) {
        for(ResourcePreListener<T> listener : preListeners){
            listener.findAll(request,response,r);
        }
	}

	/**
	 * notify handler for get
	 * @param request
	 * @param response
	 * @param r
	 */
	private void notifyGet(Request request, Response response, T r) {
        for(ResourcePreListener<T> listener : preListeners){
            listener.get(request,response,r);
        }
	}

	/**
	 * post
	 * @param request
	 * @param response
	 * @param s
	 * @throws InterruptedException 
	 */
	private void notifyPost(Request request, Response response, S s) throws InterruptedException {
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
	ApiService<S> apiService;

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
	@Override
	protected void init() {
		super.init();
		apiService = new ApiService<>(apiNeo4Service);
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

	/**
	 * get rest class
	 * @return Class<?>
	 */
	@Override
	public Class<?> getRestClass() {
		return restClass;		
	}

	@Override
	public void declare(String resource) {
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
			public Object handle(Request request, Response response) {
		    	return doFindAll(request, response);
		    }
		};
	}
		
	protected Route getResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	return doGetById(request, ID, response);
		    }
		};
	}

	protected Route postResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws InterruptedException {
		    	return doCreate(request, response, restClass);
		    }
		};
	}

	protected Route taskResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) throws TechnicalNotFoundException {
		    	return doTask(request, ID, TASK, response, restClass);
		    }
		};
	}

	protected Route putResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	return doUpdate(request, ID, response, restClass);
		    }
		};
	}

	protected Route deleteResource() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	return doDelete(request, ID, response, restClass);
		    }
		};
	}

	/**
	 * find all elements
	 * @return List<Rest>
	 */
	public List<T> doFindAllRest() {
		List<T> result = new ArrayList<>();
		for(S item : apiService.findAll()) {
			result.add(mapperFactory.getMapperFacade().map(item, restClass));
		}
		return result;
	}

	/**
	 * find all elements
	 * @param field 
	 * @param value 
	 * @return List<Rest>
	 */
	public List<T> doFindByAttributeRest(String field, String value) {
		List<T> result = new ArrayList<>();
		for(S item : apiService.findByAttribute(field, value)) {
			result.add(mapperFactory.getMapperFacade().map(item, restClass));
		}
		return result;
	}

	/**
	 * find all elements
	 * @param field 
	 * @param value 
	 * @return List<Rest>
	 */
	public List<S> doFindByAttributeBean(String field, String value) {
		List<S> result = new ArrayList<>();
		for(S item : apiService.findByAttribute(field, value)) {
			result.add(item);
		}
		return result;
	}

	/**
	 * find all elements
	 * @return List<Rest>
	 */
	public List<S> doFindAllBean() {
		List<S> result = new ArrayList<>();
		for(S item : apiService.findAll()) {
			result.add(item);
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
		S bean = apiService.getById(id);
		return mapperFactory.getMapperFacade().map(bean, beanClass);
	}
	
	/**
	 * find element by id
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException 
	 */
	public T doGetByIdRest(String id) throws TechnicalNotFoundException {
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
	 * create new entity
	 * @param b 
	 * @return Rest
	 */
	public S doCreate(S b) {
		return apiService.create(b);
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
	 * update entity
	 * @param id
	 * @param s 
	 * @return Rest
	 * @throws TechnicalNotFoundException 
	 */
	public S doUpdate(String id, S s) throws TechnicalNotFoundException {
		return apiService.update(id, s);
	}

	/**
	 * find all elements
	 * @param request
	 * @param response
	 * @return String
	 * @throws TechnicalException 
	 */
	public String doFindAll(Request request, Response response) {
		List<T> r = doFindAllRest();
		notifyFindAll(request,response,r);
    	try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			logger.error("Error {} while parsing {}", r);
			throw new TechnicalException(e);
		}
    }

	/**
	 * get element by id
	 * @param request
	 * @param id
	 * @param response
	 * @return String
	 * @throws TechnicalException 
	 */
	public String doGetById(Request request, String id, Response response) {
    	try {
    		T r = doGetByIdRest(request.params(id));
    		notifyGet(request,response,r);
    		try {
				return mapper.writeValueAsString(r);
			} catch (JsonProcessingException e) {
				logger.error("Error {} while parsing {}", r);
				throw new TechnicalException(e);
			}
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
	 * @throws InterruptedException 
	 * @throws TechnicalException 
	 */
	public String doCreate(Request request, Response response, Class<T> klass) throws InterruptedException {
    	if(request.contentType() == null || !request.contentType().contains("application/json")) {
    		response.status(403);
    		return "";
    	}
		T k;
		try {
			k = mapper.readValue(request.body(), klass);
		} catch (IOException e) {
			logger.error("Error {} while parsing {}", request.body());
			throw new TechnicalException(e);
		}
		notifyPrePost(request,response,k);
		T r = doCreate(k);
		try {
			notifyPost(request,response,mapperFactory.getMapperFacade().map(r, beanClass));
		} catch (InterruptedException e) {
			throw e;
		}
    	try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			logger.error("Error {} while parsing {}", r);
			throw new TechnicalException(e);
		}
    }

	/**
	 * update entity
	 * @param request
	 * @param id
	 * @param response
	 * @param klass
	 * @return String
	 * @throws TechnicalException 
	 */
	public String doUpdate(Request request, String id, Response response, Class<T> klass) {
    	if(request.contentType() == null || !request.contentType().contains("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
			T k;
			try {
				k = mapper.readValue(request.body(), klass);
			} catch (IOException e) {
				logger.error("Error {} while parsing {}", request.body());
				throw new TechnicalException(e);
			}
			notifyPrePut(request,response,k);
			T r = doUpdate(request.params(id), k);
			notifyPut(request,response,mapperFactory.getMapperFacade().map(r, beanClass));
	    	try {
				return mapper.writeValueAsString(r);
			} catch (JsonProcessingException e) {
				logger.error("Error {} while parsing {}", r);
				throw new TechnicalException(e);
			}
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
	 * @throws TechnicalException
	 */
	public String doDelete(Request request, String id, Response response, Class<T> klass) {
    	if(request.contentType() == null || !request.contentType().contains("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
			return writeValueAsString(doDelete(request.params(id)));
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
	 * @throws TechnicalException 
	 */
	public abstract GenericValue doRealTask(S bean, GenericMap args, TaskType taskType);

	/**
	 * execute real task on all resources, all task must be overridden in each
	 * resources
	 * 
	 * @param args
	 * @param taskType
	 * @return String
	 * @throws TechnicalException 
	 */
	public GenericValue doRealTask(GenericMap args, TaskType taskType) {
		return null;
	}

	/**
	 * create entity
	 * @param request
	 * @param id 
	 * @param task 
	 * @param response
	 * @param klass
	 * @return String
	 * @throws TechnicalException 
	 * @throws TechnicalNotFoundException 
	 */
	public String doTask(Request request, String id, String task, Response response, Class<T> klass) throws TechnicalNotFoundException {
		GenericMap body = null;
    	if(request.contentType() != null && request.contentType().startsWith("multipart/form-data")) {
    		body = new GenericMap();
    		body.put("multipart/form-data", extractMultipart(request,response));
    	} else {
	    	if(request.contentType() == null || !request.contentType().contains("application/json")) {
	    		response.status(403);
	    		return "";
	    	}
	    	if(request.queryParams(task) == null) {
	    		response.status(405);
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
    		 * task can return String, Boolean, Object or list
    		 */
    		if(GenericMap.class == result.getClass() || ArrayList.class == result.getClass() || Boolean.class == result.getClass()) {
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

	@Autowired
	CoreStatistics coreStatistics;

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
				logger.info("TASK - CONTEXT {}\n{}", beanClass.getName(), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean));
			} catch (JsonProcessingException e) {
				throw new TechnicalException(e);
			}
		} else {
			logger.info("TASK - CONTEXT {}", "*");
		}
		try {
			logger.info("TASK - INPUT\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		GenericValue result = null;
		try {
			if(bean == null) {
				/**
				 * no bean apply on all resources
				 */
				result = doRealTask(body, taskType);
			} else {
				/**
				 * task on single resource
				 */
				result = doRealTask(bean, body, taskType);
			}
		} catch (Exception e) {
			logger.error("TASK - ERROR\n", e);
			throw new TechnicalException(e);
		}
		logger.info("TASK - OUTPUT\n{}", result);
		switch(result.getType()) {
			case OBJECT:
				/**
				 * do statictics on object
				 */
				try {
					if(bean.isLogged) {
						coreStatistics.write(result.asObject(), restClass.getPackage().getName(), restClass.getSimpleName() + "." + id);
					}
				} catch(Exception e) {
					/**
					 * ignore any statistics report
					 */
				}
				return result.asObject();
			case ARRAY:
				return result.asList();
			case BOOLEAN:
				return result.asBoolean();
			case FILE_STREAM:
				response.type("application/octet-stream");
				return result.asFileStream();
			default:
				return result.asString();
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
	
	protected String writeValueAsString(Object value) {
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("Error {} while parsing {}", e, value);
			throw new TechnicalException(e);
		}
	}

	/**
	 * mount resource
	 */
	public void mount() {
		for(Declare annotation : this.getClass().getAnnotationsByType(Declare.class)) {
			logger.info("Annotated resource {}", annotation.resource());
			/**
			 * declare resource
			 */
			declare(annotation.resource());
		}
	}
}
