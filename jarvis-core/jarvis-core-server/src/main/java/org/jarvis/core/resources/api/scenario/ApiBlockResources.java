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

package org.jarvis.core.resources.api.scenario;


import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Block resource
 *
 */
@Component
public class ApiBlockResources extends ApiResources<BlockRest,BlockBean> {

	/**
	 * constructor
	 */
	public ApiBlockResources() {
		setRestClass(BlockRest.class);
		setBeanClass(BlockBean.class);
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/blocks", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/blocks/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ID, response);
		    }
		});
		post("/api/blocks", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, BlockRest.class);
		    }
		});
		put("/api/blocks/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ID, response, BlockRest.class);
		    }
		});
	}

	@Override
	public String doRealTask(BlockBean bean, GenericMap args, TaskType taskType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
