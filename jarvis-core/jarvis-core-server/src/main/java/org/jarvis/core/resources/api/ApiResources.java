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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

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

/**
 * RESOURCE api
 *
 * @param <T>
 * @param <S>
 */
public abstract class ApiResources<T extends GenericEntity,S extends GenericBean> extends ApiMapper {
	protected Logger logger = LoggerFactory.getLogger(ApiResources.class);

	private List<ResourceListener<S>> listeners = new ArrayList<ResourceListener<S>>();
	
	/**
	 * add new listener
	 * @param listener
	 */
	public void addListener(ResourceListener<S> listener) {
        listeners.add(listener);
    }

	 void notifyPost(S bean){
        for(ResourceListener<S> listener : listeners){
            try {
				listener.post(bean);
			} catch (InterruptedException e) {
				throw new TechnicalException(e);
			}
        }
	 }
	 
	@Autowired
	Environment env;
	
	@Autowired
	ApiNeo4Service apiNeo4Service;

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

	/**
	 * mount resource
	 */
	public abstract void mount();
	
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
		notifyPost(bean);
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
    	return mapper.writeValueAsString(doCreate(k));
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
	public abstract String doRealTask(S bean, GenericMap args, TaskType taskType) throws Exception;

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
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
    		Object result = doExecute(
    				request.params(id),
    				(GenericMap) mapper.readValue(request.body(),GenericMap.class),
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

	/**
	 * @param id
	 * @param body 
	 * @param taskType
	 * @return GenericMap
	 * @throws TechnicalNotFoundException
	 */
	public Object doExecute(String id, GenericMap body, TaskType taskType) throws TechnicalNotFoundException {
		/**
		 * read object by id
		 */
		S bean = null;
		if(!id.equals("*")) {
			bean = apiService.getById(id);
			try {
				logger.info("SCRIPT - CONTEXT {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean));
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
		String result = "";
		try {
			result = doRealTask(bean, body, taskType);
		} catch (Exception e) {
			throw new TechnicalException(e);
		}
		logger.info("SCRIPT - OUTPUT  {}", result);
		try {
			if(result.startsWith("{")) {
				return (GenericMap) mapper.readValue(result, GenericMap.class);
			} else {
				return result;
			}
		} catch (IOException e) {
			throw new TechnicalException(e);
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
	    	return mapper.writeValueAsString(doUpdate(request.params(id), k));
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
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
				return left.compareTo(right);
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
