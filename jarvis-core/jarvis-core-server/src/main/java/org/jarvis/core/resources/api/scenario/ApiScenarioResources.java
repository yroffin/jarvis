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

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.profiler.TaskProfiler;
import org.jarvis.core.profiler.model.GenericNode;
import org.jarvis.core.resources.api.ApiLinkedResources;
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
public class ApiScenarioResources extends ApiLinkedResources<ScenarioRest,ScenarioBean,BlockRest,BlockBean> {

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
		 * scenarios
		 */
		declare(SCENARIO_RESOURCE);
		spark.Spark.get("/api/scenarios/:id/graph", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doExecute(
	    				request.params(ID),
	    				(GenericMap) mapper.readValue("{}",GenericMap.class),
	    				TaskType.RENDER);
		    }
		});
		/**
		 * scenarios -> blocks
		 */
		declare(SCENARIO_RESOURCE, BLOCK_RESOURCE, apiBlockResources, apiHrefScenarioBlockResources, BLOCK, SORTKEY);
	}

	@Override
	public String doRealTask(ScenarioBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case EXECUTE:
				return execute(bean, args, new GenericMap());
			case RENDER:
				return render(bean, args, new GenericMap());
			default:
				result = new GenericMap();
				return mapper.writeValueAsString(result);
		}
	}

	private String execute(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		GenericMap result = args;
		for(GenericEntity entity : sort(apiHrefScenarioBlockResources.findAll(bean), "order")) {
			BlockRest block = apiBlockResources.doGetById(entity.id);
			result = apiBlockResources.execute(new GenericMap(), mapperFactory.getMapperFacade().map(block, BlockBean.class), result);
		}
		return "";
	}

	private String render(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		/**
		 * start and end node
		 */
		int index = 0;
		for(GenericEntity entity : sort(apiHrefScenarioBlockResources.findAll(bean), "order")) {
			TaskProfiler taskProfiler = new TaskProfiler();
			BlockRest block = apiBlockResources.doGetById(entity.id);
			GenericNode startNode = taskProfiler.addStartNode("start", block.name + "#1");
			GenericNode endNode = apiBlockResources.render(taskProfiler, 1, startNode, mapperFactory.getMapperFacade().map(block, BlockBean.class));
			if(index>0) sb.append(",");
			sb.append(taskProfiler.toJson());
			index++;
		}
		sb.append("]");
		return sb.toString();
	}
}
