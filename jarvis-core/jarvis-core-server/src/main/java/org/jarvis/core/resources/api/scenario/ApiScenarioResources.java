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


import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Scenario resource
 *
 */
@Component
public class ApiScenarioResources extends ApiResources<ScenarioRest,ScenarioBean> {

	@Autowired
	ApiBlockResources apiBlockResources;

	@Autowired
	ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;

	/**
	 * constructor
	 */
	public ApiScenarioResources() {
		setRestClass(ScenarioRest.class);
		setBeanClass(ScenarioBean.class);
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/scenarios", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/scenarios/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ID, response);
		    }
		});
		post("/api/scenarios", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ScenarioRest.class);
		    }
		});
		put("/api/scenarios/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ID, response, ScenarioRest.class);
		    }
		});
		/**
		 * block
		 */
		get("/api/scenarios/:id/blocks", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScenarioRest master = doGetById(request.params(ID));
		    		List<BlockRest> result = new ArrayList<BlockRest>();
		    		for(GenericEntity link : sort(apiHrefScenarioBlockResources.findAll(master), "order")) {
		    			result.add(blockRest(link));
		    		}
		    		return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		post("/api/scenarios/:id/blocks/:block", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScenarioRest iot = doGetById(request.params(ID));
			    	try {
			    		BlockRest block = apiBlockResources.doGetById(request.params(BLOCK));
				    	GenericEntity instance = apiHrefScenarioBlockResources.add(iot, block, new GenericMap(request.body()), "blocks");
				    	return mapper.writeValueAsString(blockRest(instance));
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
		put("/api/scenarios/:id/blocks/:block/:instance", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetById(request.params(ID));
			    	try {
			    		apiBlockResources.doGetById(request.params(BLOCK));
				    	GenericMap properties = apiHrefScenarioBlockResources.update(request.params(INSTANCE), new GenericMap(request.body()));
				    	return mapper.writeValueAsString(properties);
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
		put("/api/scenarios/:id/blocks/:block", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScenarioRest scenario = doGetById(request.params(ID));
			    	try {
			    		BlockRest block = apiBlockResources.doGetById(request.params(BLOCK));
				    	GenericEntity instance = apiHrefScenarioBlockResources.add(scenario, block, new GenericMap(), "scenarios");
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
		delete("/api/scenarios/:id/blocks/:block", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetById(request.params(ID));
			    	try {
			    		apiBlockResources.doGetById(request.params(BLOCK));
			    		/**
			    		 * delete this block and all its links
			    		 * TODO
			    		 * delete recursively
			    		 */
			    		apiBlockResources.doDelete(request.params(BLOCK));
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

	private BlockRest blockRest(GenericEntity instance) throws TechnicalNotFoundException {
		BlockRest blockRest = apiBlockResources.doGetById(instance.id);
		blockRest.instance = instance.instance;
		for(Entry<String, Object> property : instance.get()) {
			blockRest.put(property.getKey(), property.getValue());
		}
		return blockRest;
	}

	@Override
	public String doRealTask(ScenarioBean bean, GenericMap args, TaskType taskType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
