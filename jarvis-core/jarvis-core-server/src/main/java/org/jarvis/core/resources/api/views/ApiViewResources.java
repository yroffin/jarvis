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

package org.jarvis.core.resources.api.views;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.view.ViewBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.IotRest;
import org.jarvis.core.model.rest.view.ViewRest;
import org.jarvis.core.resources.api.ApiIotResources;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefViewResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * View resource
 */
@Component
public class ApiViewResources extends ApiResources<ViewRest,ViewBean> {

	@Autowired
	ApiIotResources apiIotResources;

	@Autowired
	ApiHrefViewResources apiHrefResources;

	/**
	 * constructor
	 */
	public ApiViewResources() {
		setRestClass(ViewRest.class);
		setBeanClass(ViewBean.class);
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/views", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/views/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ID, response);
		    }
		});
		post("/api/views", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ViewRest.class);
		    }
		});
		put("/api/views/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ID, response, ViewRest.class);
		    }
		});
		delete("/api/views/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doDelete(request, ID, response, ViewRest.class);
		    }
		});
		/**
		 * iots
		 */
		get("/api/views/:id/iots", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ViewRest view = doGetById(request.params(ID));
			    	return mapper.writeValueAsString(apiHrefResources.findAll(view, IotRest.class));
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		put("/api/views/:id/iots/:iot", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ViewRest view = doGetById(request.params(ID));
			    	try {
			    		IotRest iot = apiIotResources.doGetById(request.params(":iot"));
				    	GenericEntity instance = apiHrefResources.add(view, iot, "views");
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
		delete("/api/views/:id/iots/:iot", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ViewRest view = doGetById(request.params(ID));
			    	try {
			    		IotRest iot = apiIotResources.doGetById(request.params(":iot"));
				    	apiHrefResources.remove(view, iot, request.queryParams(INSTANCE));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ID, response);
		    }
		});
	}
}
