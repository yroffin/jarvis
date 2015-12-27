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

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import org.jarvis.core.model.bean.ScriptPluginBean;
import org.jarvis.core.model.rest.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiResources;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * script plugin resource
 */
@Component
public class ApiScriptPluginResources extends ApiResources<ScriptPluginRest,ScriptPluginBean> {

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
	}
}
