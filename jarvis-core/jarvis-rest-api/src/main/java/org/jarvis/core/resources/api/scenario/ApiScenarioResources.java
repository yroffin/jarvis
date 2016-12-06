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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.Set;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.profiler.DefaultProcessService;
import org.jarvis.core.profiler.model.DefaultProcess;
import org.jarvis.core.resources.api.ApiLinkedTwiceResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioTriggerResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Scenario resource
 *
 */
@Api(value = "scenario")
@Path("/api/scenarios")
@Produces("application/json")
@Component
@Declare(resource=ApiMapper.SCENARIO_RESOURCE, summary="Scenario resource", rest=ScenarioRest.class)
public class ApiScenarioResources extends ApiLinkedTwiceResources<ScenarioRest,ScenarioBean,BlockRest,BlockBean,TriggerRest,TriggerBean> {

	/**
	 * blocks
	 */
	@Autowired
	@DeclareLinkedResource(role=ApiMapper.BLOCK_RESOURCE, param=ApiMapper.BLOCK, sortKey=ApiMapper.SORTKEY)
	public ApiBlockResources apiBlockResources;

	/**
	 * href blocks
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.BLOCK_RESOURCE, href=ApiMapper.HREF, target=BlockRest.class)
	public ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;

	/**
	 * trigger
	 */
	@Autowired
	@DeclareLinkedResource(role=ApiMapper.TRIGGER_RESOURCE, param=ApiMapper.TRIGGER, sortKey=ApiMapper.SORTKEY)
	public ApiTriggerResources apiTriggerResources;

	/**
	 * href trigger
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.TRIGGER_RESOURCE, href=ApiMapper.HREF, target=TriggerRest.class)
	public ApiHrefScenarioTriggerResources apiHrefScenarioTriggerResources;

	@Autowired
	Environment env;

	/**
	 * constructor
	 */
	public ApiScenarioResources() {
		setRestClass(ScenarioRest.class);
		setBeanClass(ScenarioBean.class);
	}

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		super.init();
		OptionFlags.ALLOW_INCLUDE = true;
		GraphvizUtils.setDotExecutable(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env.getProperty("jarvis.dot.executable")));
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		super.mount();
		/**
		 * declare custom get
		 */
		spark.Spark.get("/api/scenarios/:id/svg", svg());
		spark.Spark.get("/api/scenarios/:id/activity", activity());
	}

	/**
	 * Render a plantuml as SVG
	 * @return Route
	 */
	@GET
	@ApiOperation(value = "Render a plantuml as SVG", nickname="graph")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType="string", name="name", paramType = "path"),
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response=GenericMap.class),
			@ApiResponse(code = 404, message = "Resource not found", response=GenericMap.class)
	})
	public Route svg() {
		return  new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doExecute(
		    			response,
	    				request.params(ID),
	    				(GenericMap) mapper.readValue("{}",GenericMap.class),
	    				TaskType.SVG);
		    }
		};
	}

	/**
	 * Render a plantuml as SVG
	 * @return Route
	 */
	@GET
	@ApiOperation(value = "Render a plantuml as text", nickname="graph")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType="string", name="name", paramType = "path"),
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response=GenericMap.class),
			@ApiResponse(code = 404, message = "Resource not found", response=GenericMap.class)
	})
	public Route activity() {
		return  new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doExecute(
		    			response,
	    				request.params(ID),
	    				(GenericMap) mapper.readValue("{}",GenericMap.class),
	    				TaskType.ACTIVITY);
		    }
		};
	}

	/**
	 * render as svg
	 * @param payload
	 * @return String
	 * @throws TechnicalException
	 */
	public String svg(String payload) throws TechnicalException {
		SourceStringReader reader = new SourceStringReader(payload);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		try {
			reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		// The XML is stored into svg
		final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
		return svg;
	}

	@Override
	public GenericValue doRealTask(ScenarioBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		switch(taskType) {
			case EXECUTE:
				try {
					return new GenericValue(execute(bean, args, new GenericMap()));
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			case SVG:
				try {
					return new GenericValue(svg(activity(bean, args, new GenericMap())));
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			case ACTIVITY:
				try {
					return new GenericValue(activity(bean, args, new GenericMap()));
				} catch (TechnicalNotFoundException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			default:
				return new GenericValue(new GenericMap());
		}
	}

	/**
	 * execute this scenario
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private String execute(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		List<String> console = new ArrayList<String>();
		for(GenericEntity entity : sort(apiHrefScenarioBlockResources.findAll(bean), "order")) {
			BlockBean block = apiBlockResources.doGetByIdBean(entity.id);
			GenericMap testParameter = null;
			try {
				if(block.testParameter != null) {
					testParameter = mapper.readValue(block.testParameter, GenericMap.class);
				} else {
					testParameter = new GenericMap();
				}
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			apiBlockResources.execute(console, 0, block, testParameter);
		}
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(console);
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
	}

	/**
	 * render this level
	 * @param stage
	 * @param entities
	 * @param processes
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private HashMap<String, GenericEntity> renderActivity(StringBuilder stage, Integer level, Collection<GenericEntity> entities, List<DefaultProcess> processes) throws TechnicalNotFoundException {
		HashMap<String, GenericEntity> calls = new HashMap<String, GenericEntity>();
		
		DefaultProcessService.addLine(stage, "start");

		/**
		 * iterate on block entities
		 */
		for(GenericEntity blockEntity : entities) {
			BlockBean block = apiBlockResources.doGetByIdBean(blockEntity.id);
			apiBlockResources.renderActivity(level, stage, block);
		}
		
		DefaultProcessService.addLine(stage, "end");

		return calls;
	}

	/**
	 * dump as uml activity text
	 * @param bean
	 * @param args
	 * @param genericMap
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private String activity(ScenarioBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		StringBuilder uml = new StringBuilder();
		uml.append("@startuml\n");
		uml.append("title "+bean.name+"\n");

		/**
		 * compute first level, return is the sub block of n-1 level
		 */
		List<DefaultProcess> processes = new ArrayList<DefaultProcess>();
		Map<String, GenericEntity> calls = renderActivity(uml, 0, sort(apiHrefScenarioBlockResources.findAll(bean), "order"), processes);
		int level = 1;
		Set<String> index = new HashSet<String>();
		while(calls.size()>0) {
			Map<String, GenericEntity> toRun = new HashMap<String, GenericEntity>();
			for(Entry<String, GenericEntity> entity : calls.entrySet()) {
				if(!index.contains(entity.getKey())) {
					index.add(entity.getKey());
					toRun.put(entity.getKey(), entity.getValue());
				}
			}
			/**
			 * render not known block
			 */
			calls = renderActivity(uml, level, toRun.values(), processes);
			level ++;
			if(level > 10) {
				logger.warn("To many levels {}", bean.id);
				break;
			}
		}

		uml.append("@enduml\n");
		return uml.toString();
	}
}
