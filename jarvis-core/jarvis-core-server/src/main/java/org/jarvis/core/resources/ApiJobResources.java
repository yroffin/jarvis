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

package org.jarvis.core.resources;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.List;

import org.jarvis.core.model.rest.JobRest;
import org.jarvis.core.services.ApiJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

@Component
public class ApiJobResources extends ApiResources<JobRest> {

	@Autowired
	ApiJobService apiJobService;

	@Override
	public List<JobRest> doFindAll() {
    	return apiJobService.findAll();
	}

	@Override
	public JobRest doGetById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobRest doCreate(JobRest jobRest) {
		return apiJobService.create(jobRest);
	}

	@Override
	public JobRest doUpdate(String id, JobRest jobRest) {
    	return apiJobService.update(id, jobRest);
	}

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
	}
}
