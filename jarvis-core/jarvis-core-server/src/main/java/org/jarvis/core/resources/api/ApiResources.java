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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

public abstract class ApiResources<Klass> {
	@Autowired
	Environment env;
	
	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * mount resource
	 */
	public abstract void mount();
	
	/**
	 * find all elements
	 * @return
	 */
	public abstract List<Klass> doFindAll();
	
	/**
	 * find element by id
	 * @param id
	 * @return
	 */
	public abstract Klass doGetById(String id);
	
	/**
	 * create new entity
	 * @param k
	 * @return
	 */
	public abstract Klass doCreate(Klass k);
	
	/**
	 * update entity
	 * @param id
	 * @param k
	 * @return
	 */
	public abstract Klass doUpdate(String id, Klass k);

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
	public Object doCreate(Request request, Response response, Class<Klass> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		Klass k = mapper.readValue(request.body(), klass);
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
	public Object doUpdate(Request request, String id, Response response, Class<Klass> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		Klass k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doUpdate(request.params(id), k));
    }
}
