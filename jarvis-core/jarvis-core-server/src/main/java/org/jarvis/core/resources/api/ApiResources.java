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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.services.ApiService;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.services.shell.PluginShellService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import spark.Request;
import spark.Response;

/**
 * RESOURCE api
 *
 * @param <Rest>
 * @param <Bean>
 */
public abstract class ApiResources<Rest,Bean> extends ApiMapper {
	protected Logger logger = LoggerFactory.getLogger(ApiResources.class);

	protected static final String IOT = ":iot";
	protected static final String ID = ":id";
	protected static final String PARAM = ":param";
	protected static final String INSTANCE = "instance";

	@Autowired
	Environment env;
	
	@Autowired
	ApiNeo4Service apiNeo4Service;

	/**
	 * internal api service
	 */
	ApiService<Bean> apiService = new ApiService<Bean>();

	/**
	 * bean class
	 */
	private Class<Bean> beanClass;

	/**
	 * rest class
	 */
	private Class<Rest> restClass;

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
	protected void setBeanClass(Class<Bean> klass) {
		beanClass = klass;
	}

	/**
	 * set rest class
	 * @param klass
	 */
	protected void setRestClass(Class<Rest> klass) {
		restClass = klass;		
	}

	/**
	 * mount resource
	 */
	public abstract void mount();
	
	/**
	 * find all elements
	 * @return List<Rest>
	 */
	public List<Rest> doFindAll() {
		List<Rest> result = new ArrayList<Rest>();
		for(Bean item : apiService.findAll()) {
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
	public Rest doGetById(String id) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(apiService.getById(id), restClass);
	}
	
	/**
	 * create new entity
	 * @param r 
	 * @return Rest
	 */
	public Rest doCreate(Rest r) {
		return mapperFactory.getMapperFacade().map(
				apiService.create(mapperFactory.getMapperFacade().map(r, beanClass)),
				restClass);
	}
	
	/**
	 * delete by id
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public Rest doDelete(String id) throws TechnicalNotFoundException {
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
	public Rest doUpdate(String id, Rest r) throws TechnicalNotFoundException {
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
	public String doCreate(Request request, Response response, Class<Rest> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		Rest k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doCreate(k));
    }

	@Autowired
	PluginShellService pluginShellService;

	@Autowired
	PluginGroovyService pluginGroovyService;

	/**
	 * create entity
	 * @param request
	 * @param id 
	 * @param task 
	 * @param response
	 * @param klass
	 * @return String
	 * @throws Exception
	 */
	public String doTask(Request request, String id, String task, Response response, Class<Rest> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
    		/**
    		 * decode body
    		 */
    		GenericMap args = (GenericMap) mapper.readValue(request.body(), GenericMap.class);
    		logger.info("SCRIPT - INPUT   {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(args));
    		/**
    		 * read object by id
    		 */
    		Bean bean = apiService.getById(request.params(id));
    		logger.info("SCRIPT - CONTEXT {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean));
    		/**
    		 * convert command to lazy map
    		 */
    		GenericMap converted = new GenericMap();
    		for(Field field : bean.getClass().getDeclaredFields()) {
    			converted.put(field.getName(), field.get(bean));
    		}
    		GenericMap result = null;
    		switch(TaskType.valueOf(request.queryParams(task).toUpperCase())) {
    			case COMMAND:
    				result = pluginShellService.command(converted, args);
    				break;
    			case SHELL:
    				result = pluginShellService.shell(converted, args);
    				break;
    			case GROOVY:
    				result = pluginGroovyService.groovy(converted, args);
    				break;
    			default:
    		}
    		logger.info("SCRIPT - OUTPUT  {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        	return mapper.writeValueAsString(result);
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
	public String doDelete(Request request, String id, Response response, Class<Rest> klass) throws Exception {
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
	public String doUpdate(Request request, String id, Response response, Class<Rest> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	try {
			Rest k = mapper.readValue(request.body(), klass);
	    	return mapper.writeValueAsString(doUpdate(request.params(id), k));
    	} catch(TechnicalNotFoundException e) {
    		response.status(404);
    		return "";
    	}
    }
}
