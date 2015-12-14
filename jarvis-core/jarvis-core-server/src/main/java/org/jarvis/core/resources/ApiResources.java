package org.jarvis.core.resources;

import java.util.List;

import org.jarvis.core.model.rest.JobRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;
import spark.Response;

public abstract class ApiResources<Klass> {
	@Autowired
	Environment env;
	
	protected ObjectMapper mapper = new ObjectMapper();

	public abstract List<Klass> doFindAll();
	public abstract Klass doGetById(String id);
	public abstract Klass doCreate(Klass k);
	public abstract Klass doUpdate(String id, Klass k);

	/**
	 * find all elements
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Object doFindAll(Request request, Response response) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
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

	public Object doCreate(Request request, Response response, Class<Klass> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		Klass k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doCreate(k));
    }

	public Object doUpdate(Request request, String id, Response response, Class<Klass> klass) throws Exception {
    	if(request.contentType() == null || !request.contentType().equals("application/json")) {
    		response.status(403);
    		return "";
    	}
		Klass k = mapper.readValue(request.body(), klass);
    	return mapper.writeValueAsString(doUpdate(request.params(id), k));
    }
}
