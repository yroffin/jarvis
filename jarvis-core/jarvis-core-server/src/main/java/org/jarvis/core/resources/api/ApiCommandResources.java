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

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Commande resources
 */
@Component
public class ApiCommandResources extends ApiResources<CommandRest,CommandBean> {

	/**
	 * constructor
	 */
	public ApiCommandResources() {
		setRestClass(CommandRest.class);
		setBeanClass(CommandBean.class);
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/commands", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/commands/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/commands", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, CommandRest.class);
		    }
		});
		put("/api/commands/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, CommandRest.class);
		    }
		});
		delete("/api/commands/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ":id", response, CommandRest.class);
		    }
		});
	}
}
