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

package org.jarvis.core.resources.api.plugins;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.ScriptPluginRest;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.resources.api.ApiCommandResources;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefPluginResources;
import org.jarvis.core.resources.api.jobs.ApiParamResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * script plugin resource
 */
@Component
public class ApiScriptPluginResources extends ApiResources<ScriptPluginRest,ScriptPluginBean> {

	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiHrefPluginResources apiHrefResources;

	/**
	 * constructor
	 */
	public ApiScriptPluginResources() {
		setRestClass(ScriptPluginRest.class);
		setBeanClass(ScriptPluginBean.class);
	}
	
	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/plugins/scripts", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/plugins/scripts/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/plugins/scripts", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ScriptPluginRest.class);
		    }
		});
		put("/api/plugins/scripts/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, ScriptPluginRest.class);
		    }
		});
		/**
		 * params
		 */
		get("/api/plugins/scripts/:id/commands", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest master = doGetById(request.params(":id"));
			    	return mapper.writeValueAsString(apiHrefResources.findAll(master, CommandRest.class));
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		put("/api/plugins/scripts/:id/commands/:command", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest job = doGetById(request.params(":id"));
			    	try {
			    		CommandRest param = apiCommandResources.doGetById(request.params(":param"));
				    	GenericEntity instance = apiHrefResources.add(job, param);
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
		delete("/api/plugins/scripts/:id/commands/:command", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest job = doGetById(request.params(":id"));
			    	try {
			    		CommandRest param = apiCommandResources.doGetById(request.params(":param"));
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
}
