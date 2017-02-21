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

import com.fasterxml.jackson.core.JsonProcessingException;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

/**
 * standard resource api
 * 
 * @param <REST>
 * @param <BEAN>
 */
public abstract class ApiResources<REST extends GenericEntity, BEAN extends GenericBean> extends ApiGenericResources
		implements ApiDefaultResources {
	protected Logger logger = LoggerFactory.getLogger(ApiResources.class);

	private List<ResourcePreListener<REST>> preListeners = new ArrayList<>();
	private List<ResourcePostListener<REST, BEAN>> postListeners = new ArrayList<>();

	@Autowired
	CoreStatistics coreStatistics;

	/**
	 * mapper
	 * 
	 * @param bean
	 * @return REST
	 */
	public REST mapBeanToRest(BEAN bean) {
		return mapperFactory.getMapperFacade().map(bean, restClass);
	}

	/**
	 * mapper
	 * 
	 * @param rest
	 * @return BEAN
	 */
	public BEAN mapRestToBean(REST rest) {
		return mapperFactory.getMapperFacade().map(rest, beanClass);
	}

	/**
	 * add new listener
	 * 
	 * @param listener
	 */
	public void addPreListener(ResourcePreListener<REST> listener) {
		preListeners.add(listener);
	}

	/**
	 * add new listener
	 * 
	 * @param listener
	 */
	public void addPostListener(ResourcePostListener<REST, BEAN> listener) {
		postListeners.add(listener);
	}

	/**
	 * all ressource method
	 */
	private enum TRIGGER_METHOD {
		POST, PUT, GET, DELETE, ALL
	}

	/**
	 * pre notification
	 * 
	 * @param request
	 * @param response
	 * @param t
	 */
	private void triggerBefore(TRIGGER_METHOD method, Request request, Response response, REST rest) {
		switch (method) {
		case POST:
			for (ResourcePreListener<REST> listener : preListeners) {
				listener.post(request, response, rest);
			}
			break;
		case PUT:
			for (ResourcePreListener<REST> listener : preListeners) {
				listener.put(request, response, rest);
			}
			break;
		case GET:
			for (ResourcePreListener<REST> listener : preListeners) {
				listener.get(request, response, rest);
			}
			break;
		default:
			logger.warn("No such post treatment for {}", method);
			break;
		}
	}

	/**
	 * post
	 * 
	 * @param request
	 * @param response
	 * @param s
	 * @throws InterruptedException
	 */
	private void triggerAfterBean(TRIGGER_METHOD method, BEAN bean) {
		switch (method) {
		case POST:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.postBean(bean);
			}
			break;
		case PUT:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.putBean(bean);
			}
			break;
		case GET:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.getBean(bean);
			}
			break;
		case ALL:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.getAllBean(bean);
			}
			break;
		default:
			logger.warn("No such post treatment for {}", method);
			break;
		}
	}

	/**
	 * trigger action after rest result
	 * 
	 * @param method
	 * @param request
	 * @param response
	 * @param rest
	 */
	private void triggerAfterRest(TRIGGER_METHOD method, Request request, Response response, REST rest) {
		switch (method) {
		case POST:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.postRest(request, response, rest);
			}
			break;
		case PUT:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.putRest(request, response, rest);
			}
			break;
		case GET:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.getRest(request, response, rest);
			}
			break;
		case ALL:
			for (ResourcePostListener<REST, BEAN> listener : postListeners) {
				listener.getAllRest(request, response, rest);
			}
			break;
		default:
			logger.warn("No such post treatment for {}", method);
			break;
		}
	}

	@Autowired
	Environment env;

	@Autowired
	protected ApiNeo4Service apiNeo4Service;

	/**
	 * internal api service
	 */
	ApiService<BEAN> apiService;

	/**
	 * bean class
	 */
	private Class<BEAN> beanClass;

	/**
	 * rest class
	 */
	private Class<REST> restClass;

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
	 * 
	 * @param klass
	 */
	protected void setBeanClass(Class<BEAN> klass) {
		beanClass = klass;
	}

	/**
	 * set rest class
	 * 
	 * @param klass
	 */
	protected void setRestClass(Class<REST> klass) {
		restClass = klass;
	}

	/**
	 * get rest class
	 * 
	 * @return Class<?>
	 */
	@Override
	public Class<?> getRestClass() {
		return restClass;
	}

	@Override
	public void declare(String resource) {
		get("/api/" + resource + "", getResources());
		get("/api/" + resource + "/:id", getResource());
		post("/api/" + resource + "", postResource());
		post("/api/" + resource + "/:id", taskResource());
		put("/api/" + resource + "/:id", putResource());
		delete("/api/" + resource + "/:id", deleteResource());
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
				return doCreate(request, response);
			}
		};
	}

	protected Route taskResource() {
		return new Route() {
			@Override
			public Object handle(Request request, Response response) throws TechnicalNotFoundException {
				return doTask(request, ID, TASK, response);
			}
		};
	}

	protected Route putResource() {
		return new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return doUpdate(request, ID, response);
			}
		};
	}

	protected Route deleteResource() {
		return new Route() {
			@Override
			public Object handle(Request request, Response response) {
				return doDelete(request, ID, response);
			}
		};
	}

	/**
	 * find all elements
	 * 
	 * @return List<Rest>
	 */
	public List<REST> doFindAllRest() {
		List<REST> result = new ArrayList<>();
		for (BEAN bean : doFindAllBean()) {
			result.add(mapBeanToRest(bean));
		}
		return result;
	}

	/**
	 * find all elements
	 * 
	 * @param field
	 * @param value
	 * @return List<Rest>
	 */
	public List<REST> doFindByAttributeRest(String field, String value) {
		List<REST> result = new ArrayList<>();
		for (BEAN bean : doFindByAttributeBean(field, value)) {
			result.add(mapBeanToRest(bean));
		}
		return result;
	}

	/**
	 * find element by id
	 * 
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public REST doGetByIdRest(String id) throws TechnicalNotFoundException {
		return mapBeanToRest(doGetByIdBean(id));
	}

	/**
	 * update entity
	 * 
	 * @param id
	 * @param rest
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public BEAN doUpdateRest(String id, REST rest) throws TechnicalNotFoundException {
		return doUpdateBean(id, mapRestToBean(rest));
	}

	/**
	 * find all elements
	 * 
	 * @param request
	 * @param response
	 * @return String
	 * @throws TechnicalException
	 */
	public String doFindAll(Request request, Response response) {
		List<BEAN> beans = doFindAllBean();
		List<REST> rests = new ArrayList<>();
		for (BEAN bean : beans) {
			REST rest = mapBeanToRest(bean);
			rests.add(rest);
			triggerAfterRest(TRIGGER_METHOD.ALL, request, response, rest);
		}
		return writeValueAsString(rests);
	}

	/**
	 * get element by id
	 * 
	 * @param request
	 * @param id
	 * @param response
	 * @return String
	 * @throws TechnicalException
	 */
	public String doGetById(Request request, String id, Response response) {
		try {
			BEAN bean = doGetByIdBean(request.params(id));
			REST rest = mapBeanToRest(bean);
			triggerAfterRest(TRIGGER_METHOD.GET, request, response, rest);
			return writeValueAsString(rest);
		} catch (TechnicalNotFoundException e) {
			response.status(404);
			return "";
		}
	}

	/**
	 * create entity
	 * 
	 * @param request
	 * @param response
	 * @return String
	 * @throws InterruptedException
	 * @throws TechnicalException
	 */
	public String doCreate(Request request, Response response) throws InterruptedException {
		if (request.contentType() == null || !request.contentType().contains("application/json")) {
			response.status(403);
			return "";
		}
		REST rest;
		try {
			rest = mapper.readValue(request.body(), restClass);
		} catch (IOException e) {
			logger.error("Error {} while parsing {}", request.body());
			throw new TechnicalException(e);
		}
		triggerBefore(TRIGGER_METHOD.POST, request, response, rest);
		BEAN created = doCreateBean(mapRestToBean(rest));
		REST result = mapBeanToRest(created);
		triggerAfterRest(TRIGGER_METHOD.POST, request, response, result);
		return writeValueAsString(result);
	}

	/**
	 * update entity
	 * 
	 * @param request
	 * @param id
	 * @param response
	 * @return String
	 * @throws TechnicalException
	 */
	public String doUpdate(Request request, String id, Response response) {
		if (request.contentType() == null || !request.contentType().contains("application/json")) {
			response.status(403);
			return "";
		}
		try {
			REST rest;
			try {
				rest = mapper.readValue(request.body(), restClass);
			} catch (IOException e) {
				logger.error("Error {} while parsing {}", request.body());
				throw new TechnicalException(e);
			}
			triggerBefore(TRIGGER_METHOD.PUT, request, response, rest);
			BEAN updated = doUpdateRest(request.params(id), rest);
			REST result = mapBeanToRest(updated);
			triggerAfterRest(TRIGGER_METHOD.PUT, request, response, result);
			return writeValueAsString(result);
		} catch (TechnicalNotFoundException e) {
			response.status(404);
			return "";
		}
	}

	/**
	 * delete node
	 * 
	 * @param request
	 * @param id
	 * @param response
	 * @return String
	 * @throws TechnicalException
	 */
	public String doDelete(Request request, String id, Response response) {
		if (request.contentType() == null || !request.contentType().contains("application/json")) {
			response.status(403);
			return "";
		}
		try {
			return writeValueAsString(doDelete(request.params(id)));
		} catch (TechnicalNotFoundException e) {
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
	public abstract GenericValue doRealTask(BEAN bean, GenericMap args, TaskType taskType);

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
	 * 
	 * @param request
	 * @param id
	 * @param task
	 * @param response
	 * @return String
	 * @throws TechnicalException
	 * @throws TechnicalNotFoundException
	 */
	public String doTask(Request request, String id, String task, Response response) throws TechnicalNotFoundException {
		GenericMap body = null;
		if (request.contentType() != null && request.contentType().startsWith("multipart/form-data")) {
			body = new GenericMap();
			body.put("multipart/form-data", extractMultipart(request, response));
		} else {
			if (request.contentType() == null || !request.contentType().contains("application/json")) {
				response.status(403);
				return "";
			}
			if (request.queryParams(task) == null) {
				response.status(405);
				return "";
			}
			try {
				body = (GenericMap) mapper.readValue(request.body(), GenericMap.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
		}
		try {
			Object result = doExecute(response, request.params(id), body,
					TaskType.valueOf(request.queryParams(task).toUpperCase()));
			/**
			 * task can return String, Boolean, Object or list
			 */
			if (GenericMap.class == result.getClass() || ArrayList.class == result.getClass()
					|| Boolean.class == result.getClass()) {
				return writeValueAsString(result);
			} else {
				return (String) result;
			}
		} catch (TechnicalNotFoundException e) {
			response.status(404);
			return "";
		}
	}

	/**
	 * multi part extract
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private String extractMultipart(Request request, Response response) {
		String location = "/tmp"; // the directory location where files will be
									// stored
		long maxFileSize = 100000000; // the maximum size allowed for uploaded
										// files
		long maxRequestSize = 100000000; // the maximum size allowed for
											// multipart/form-data requests
		int fileSizeThreshold = 1024; // the size threshold after which files
										// will be written to disk
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize,
				maxRequestSize, fileSizeThreshold);
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
	public Object doExecute(Response response, String id, GenericMap body, TaskType taskType)
			throws TechnicalNotFoundException {
		GenericValue result = rawExecute(id, body, taskType);
		List<?> res;
		switch (result.getType()) {
		case OBJECT:
			response.type("application/json");
			return result.asObject();
		case ARRAY:
			response.type("application/json");
			res = result.asList();
			response.header("X-Total-Count", res.size()+"");
			return res;
		case BOOLEAN:
			response.type("application/json");
			return result.asBoolean();
		case FILE_STREAM:
			response.type("application/octet-stream");
			return result.asFileStream();
		default:
			return result.asString();
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
		GenericValue result = rawExecute(id, body, taskType);
		switch (result.getType()) {
		case OBJECT:
			return result.asObject();
		case ARRAY:
			return result.asList();
		case BOOLEAN:
			return result.asBoolean();
		case FILE_STREAM:
			return result.asFileStream();
		default:
			return result.asString();
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
	private GenericValue rawExecute(String id, GenericMap body, TaskType taskType) throws TechnicalNotFoundException {
		/**
		 * read object by id
		 */
		BEAN bean = null;
		if (!id.equals("*")) {
			bean = doGetByIdBean(id);
			logger.info("TASK - CONTEXT {}\n{}", beanClass.getName(), writeValueAsString(bean));
		} else {
			logger.info("TASK - CONTEXT {}", "*");
		}
		logger.info("TASK - INPUT\n{}", writeValueAsString(body));
		GenericValue result = null;
		try {
			if (bean == null) {
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

		switch (result.getType()) {
		case OBJECT:
			/**
			 * do statictics on object
			 */
			if (bean.isLogged) {
				coreStatistics.write(result.asObject(), restClass.getPackage().getName(),
						restClass.getSimpleName() + "." + id);
			}
		default:
		}

		return result;
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
				if (left == null) {
					return -1;
				}
				String right = (String) r.get(field);
				if (right != null) {
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
	 * 
	 * @param link
	 * @return ScriptPluginRest
	 * @throws TechnicalNotFoundException
	 */
	protected GenericEntity fromLink(GenericEntity link, GenericEntity rest) throws TechnicalNotFoundException {
		rest.instance = link.instance;
		for (Entry<String, Object> property : link.get()) {
			rest.put(property.getKey(), property.getValue());
		}
		return rest;
	}

	/**
	 * find all elements
	 * 
	 * @param field
	 * @param value
	 * @return List<Rest>
	 */
	public List<BEAN> doFindByAttributeBean(String field, String value) {
		List<BEAN> result = new ArrayList<>();
		for (BEAN bean : apiService.findByAttribute(field, value)) {
			triggerAfterBean(TRIGGER_METHOD.ALL, bean);
			result.add(bean);
		}
		return result;
	}

	/**
	 * find all elements
	 * 
	 * @return List<Rest>
	 */
	public List<BEAN> doFindAllBean() {
		List<BEAN> result = new ArrayList<>();
		for (BEAN bean : apiService.findAll()) {
			triggerAfterBean(TRIGGER_METHOD.ALL, bean);
			result.add(bean);
		}
		return result;
	}

	/**
	 * find element by id
	 * 
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public BEAN doGetByIdBean(String id) throws TechnicalNotFoundException {
		BEAN bean = apiService.getById(id);
		triggerAfterBean(TRIGGER_METHOD.GET, bean);
		return bean;
	}

	/**
	 * update entity
	 * 
	 * @param id
	 * @param bean
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public BEAN doUpdateBean(String id, BEAN bean) throws TechnicalNotFoundException {
		BEAN updated = apiService.update(id, bean);
		return updated;
	}

	/**
	 * create new entity
	 * 
	 * @param bean
	 * @return Rest
	 */
	public BEAN doCreateBean(BEAN bean) {
		BEAN created = apiService.create(bean);
		triggerAfterBean(TRIGGER_METHOD.POST, created);
		return created;
	}

	/**
	 * delete by id
	 * 
	 * @param id
	 * @return Rest
	 * @throws TechnicalNotFoundException
	 */
	public REST doDelete(String id) throws TechnicalNotFoundException {
		BEAN deleted = apiService.remove(id);
		triggerAfterBean(TRIGGER_METHOD.DELETE, deleted);
		return mapBeanToRest(deleted);
	}

	/**
	 * common function
	 * 
	 * @param value
	 * @return
	 */
	protected String writeValueAsString(Object value) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
		} catch (JsonProcessingException e) {
			logger.error("Error {} while parsing {}", e, value);
			throw new TechnicalException(e);
		}
	}

	/**
	 * mount resource
	 */
	public void mount() {
		for (Declare annotation : this.getClass().getAnnotationsByType(Declare.class)) {
			logger.info("Annotated resource {}", annotation.resource());
			/**
			 * declare resource
			 */
			declare(annotation.resource());
		}
	}
}
