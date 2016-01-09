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

import org.jarvis.core.model.bean.job.ParamBean;
import org.jarvis.core.model.rest.job.ParamRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

@Component
public class ApiParamResources extends ApiResources<ParamRest,ParamBean> {

	/**
	 * constructor
	 */
	public ApiParamResources() {
		setRestClass(ParamRest.class);
		setBeanClass(ParamBean.class);
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/params", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/params/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/params", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ParamRest.class);
		    }
		});
		put("/api/params/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, ParamRest.class);
		    }
		});
		delete("/api/params/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ":id", response, ParamRest.class);
		    }
		});
	}

	@Override
	public String doRealTask(ParamBean bean, GenericMap args, TaskType taskType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}