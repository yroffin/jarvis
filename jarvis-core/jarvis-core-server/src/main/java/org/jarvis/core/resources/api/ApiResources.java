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

import java.util.List;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.services.ApiService;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import spark.Request;
import spark.Response;

public abstract class ApiResources<T,K> {
	@Autowired
	Environment env;
	
	@Autowired
	ApiNeo4Service apiNeo4Service;

	protected ObjectMapper mapper = new ObjectMapper();
	MapperFactory mapperFactory = null;

	/**
	 * internal api service
	 */
	ApiService<K> apiService = new ApiService<K>();

	@PostConstruct
	void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		apiService.setApiNeo4Service(apiNeo4Service);
	}

	/**
	 * mount resource
	 */
	public abstract void mount();
	
	/**
	 * find all elements
	 * @return
	 */
	public abstract List<T> doFindAll();
	
	/**
	 * find element by id
	 * @param id
	 * @return
	 * @throws TechnicalNotFoundException 
	 */
	public abstract T doGetById(String id) throws TechnicalNotFoundException;
	
	/**
	 * create new entity
	 * @param k
	 * @return
	 */
	public abstract T doCreate(T k);
	
	/**
	 * update entity
	 * @param id
	 * @param k
	 * @return
	 * @throws TechnicalNotFoundException 
	 */
	public abstract T doUpdate(String id, T k) throws TechnicalNotFoundException;

	/**
	 * find all elements
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Object doFindAll(Request request, Response response) throws Exception {
    	return mapper.writeValueAsString(doFindAll());
    }

	/**
	 * get element by id
	 * @param request
	 * @param id
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Object doGetById(Request request, String id, Response response) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
    	return mapper.writeValueAsString(doGetById(request.params(id)));
    }

	/**
	 * create entity
	 * @param request
	 * @param response
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public Object doCreate(Request request, Response response, Class<T> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		T k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doCreate(k));
    }

	/**
	 * update entity
	 * @param request
	 * @param id
	 * @param response
	 * @param klass
	 * @return
	 * @throws Exception
	 */
	public Object doUpdate(Request request, String id, Response response, Class<T> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		T k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doUpdate(request.params(id), k));
    }
}
