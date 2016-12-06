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

package org.jarvis.core.resources.api.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourceDefaultPostListenerImpl;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.resources.api.href.ApiHrefDeviceTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefTriggerCronResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.jarvis.core.resources.api.tools.ApiCronResources;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * Trigger resource
 */
@Api(value = "trigger")
@Path("/api/triggers")
@Produces("application/json")
@Component
@Declare(resource=ApiMapper.TRIGGER_RESOURCE, summary="Trigger resource", rest=TriggerRest.class)
public class ApiTriggerResources extends ApiLinkedResources<TriggerRest,TriggerBean,CronRest,CronBean> {

	/**
	 * cron resource link
	 */
	@Autowired
	@DeclareLinkedResource(role=ApiMapper.CRON_RESOURCE, param=ApiMapper.CRON, sortKey=ApiMapper.SORTKEY)
	public ApiCronResources apiCronResources;
	
	/**
	 * cron resource href
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.CRON_RESOURCE, href=ApiMapper.HREF, target=CronRest.class)
	public ApiHrefTriggerCronResources apiHrefTriggerCronResources;

	@Autowired
	CoreEventDaemon coreEventDaemon;
	
	@Autowired
	ApiDeviceResources apiDeviceResources;

	@Autowired
	ApiHrefDeviceTriggerResources apiHrefDeviceTriggerResources;
	
	@Autowired
	ApiScenarioResources apiScenarioResources;

	@Autowired
	ApiHrefScenarioTriggerResources apiHrefDeviceScenarioResources;

	/**
	 * constructor
	 */
	public ApiTriggerResources() {
		setRestClass(TriggerRest.class);
		setBeanClass(TriggerBean.class);
	}

	class ResourceListenerImpl extends ResourceDefaultPostListenerImpl<TriggerRest, TriggerBean> implements ResourcePostListener<TriggerRest, TriggerBean> {

		@Override
		public void getRest(Request request, Response response, TriggerRest trigger) {
			trigger.devices = new ArrayList<>();
			for(DeviceBean device : apiDeviceResources.doFindAllBean()) {
				for(GenericEntity href : apiHrefDeviceTriggerResources.findAll(device)) {
					if(href.id.equals(trigger.id)) {
						trigger.devices.add(apiDeviceResources.mapBeanToRest(device));
					}
				}
			}
			trigger.scenarii = new ArrayList<>();
			for(ScenarioBean scenario : apiScenarioResources.doFindAllBean()) {
				for(GenericEntity href : apiHrefDeviceScenarioResources.findAll(scenario)) {
					if(href.id.equals(trigger.id)) {
						trigger.scenarii.add(apiScenarioResources.mapBeanToRest(scenario));
					}
				}
			}
		}

	}

	@Override
	public void mount() {
		super.mount();
		/**
		 * patch
		 */
		Spark.patch("/api/"+TRIGGER_RESOURCE+"/:name", trigger());
		/**
		 * declare listener
		 */
		addPostListener(new ResourceListenerImpl());
	}

	/**
	 * local trigger method
	 * @return Route
	 */
	@io.swagger.jaxrs.PATCH
	@ApiOperation(value = "Activate a new trigger", nickname="trigger")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType="string", name="name", paramType = "path"),
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response=GenericMap.class),
			@ApiResponse(code = 404, message = "Trigger not found", response=GenericMap.class)
	})
	public Route trigger() {
		return new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	String name = request.params(":name");
		    	List<TriggerBean> triggers = doFindByAttributeBean("name", name);
		    	/**
		    	 * only one row accepted
		    	 */
		    	if(triggers.size() != 1) {
		    		response.status(404);
		    		return "";
		    	}
		    	/**
		    	 * execute it
		    	 */
		    	try {
					execute(triggers.get(0),(GenericMap) mapper.readValue(request.body(),GenericMap.class));
		    		return "";
				} catch (IOException e) {
					throw new TechnicalException(e);
				}
		    }
		};
	}

	@Override
	public GenericValue doRealTask(TriggerBean trigger, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result = args;
		switch(taskType) {
			case EXECUTE:
				try {
					return new GenericValue(mapper.writeValueAsString(execute(trigger, result)));
				} catch (JsonProcessingException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * execute this trigger
	 * @param trigger
	 * @param args
	 * @return
	 */
	private GenericMap execute(TriggerBean trigger, GenericMap args) {
		try {
			coreEventDaemon.post(trigger.id, trigger.name);
		} catch (InterruptedException e) {
			logger.warn("Error {}", e);
			Thread.currentThread().interrupt();
		}
		return args;
	}
}
