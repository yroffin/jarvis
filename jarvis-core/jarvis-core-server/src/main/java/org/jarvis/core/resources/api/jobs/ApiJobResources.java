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

package org.jarvis.core.resources.api.jobs;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.JobBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.JobRest;
import org.jarvis.core.model.rest.job.ParamRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefJobResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * JOB resource
 */
@Component
public class ApiJobResources extends ApiResources<JobRest,JobBean> {

	@Autowired
	ApiParamResources apiParamResources;

	@Autowired
	ApiHrefJobResources apiHrefResources;

	/**
	 * constructor
	 */
	public ApiJobResources() {
		setRestClass(JobRest.class);
		setBeanClass(JobBean.class);
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/jobs", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/jobs/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/jobs", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, JobRest.class);
		    }
		});
		put("/api/jobs/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, JobRest.class);
		    }
		});
		delete("/api/jobs/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ":id", response, JobRest.class);
		    }
		});
		/**
		 * params
		 */
		get("/api/jobs/:id/params", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
			    	JobRest job = doGetById(request.params(":id"));
			    	return mapper.writeValueAsString(apiHrefResources.findAll(job, ParamRest.class));
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		put("/api/jobs/:id/params/:param", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
			    	JobRest job = doGetById(request.params(":id"));
			    	try {
				    	ParamRest param = apiParamResources.doGetById(request.params(":param"));
				    	GenericEntity instance = apiHrefResources.add(job, param, "params");
				    	return mapper.writeValueAsString(instance);
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		delete("/api/jobs/:id/params/:param", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
			    	JobRest job = doGetById(request.params(":id"));
			    	try {
				    	ParamRest param = apiParamResources.doGetById(request.params(":param"));
				    	apiHrefResources.remove(job, param, request.queryParams("instance"));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ":id", response);
		    }
		});
	}

	@Override
	public String doRealTask(JobBean bean, GenericMap args, TaskType taskType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
