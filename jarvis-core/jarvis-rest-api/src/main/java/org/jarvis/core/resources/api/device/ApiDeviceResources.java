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

import static spark.Spark.get;

import java.util.Map;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.device.DeviceRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.resources.api.ApiLinkedThirdResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefDeviceResources;
import org.jarvis.core.resources.api.href.ApiHrefDeviceScriptPluginResources;
import org.jarvis.core.resources.api.href.ApiHrefDeviceTriggerResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * device resource
 */
@Component
public class ApiDeviceResources extends ApiLinkedThirdResources<DeviceRest,DeviceBean,DeviceRest,DeviceBean,ScriptPluginRest,ScriptPluginBean,TriggerRest,TriggerBean> {

	@Autowired
	ApiHrefDeviceResources apiHrefDeviceResources;

	@Autowired
	ApiHrefDeviceScriptPluginResources apiHrefDeviceScriptPluginResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	@Autowired
	ApiHrefDeviceTriggerResources apiHrefDeviceTriggerResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	/**
	 * constructor
	 */
	public ApiDeviceResources() {
		setRestClass(DeviceRest.class);
		setBeanClass(DeviceBean.class);
	}

	@Override
	public void mount() {
		/**
		 * scripts
		 */
		declare(DEVICE_RESOURCE);
		/**
		 * scripts -> commands
		 */
		declare(DEVICE_RESOURCE, DEVICE_RESOURCE, this, apiHrefDeviceResources, DEVICE, SORTKEY, HREF);
		declareSecond(DEVICE_RESOURCE, SCRIPT_RESOURCE, apiScriptPluginResources, apiHrefDeviceScriptPluginResources, PLUGIN, SORTKEY, HREF);
		declareThird(DEVICE_RESOURCE, TRIGGER_RESOURCE, apiTriggerResources, apiHrefDeviceTriggerResources, TRIGGER, SORTKEY, HREF);
		/**
		 * device html generator
		 */
		get("/api/directives/html/devices/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	DeviceRest device = doGetByIdRest(request.params(ID));
		    	return device.template;
		    }
		});
	}

	@Override
	public GenericValue doRealTask(DeviceBean device, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result;
		switch(taskType) {
			case RENDER:
				try {
					result = render(device, args);
				} catch (Exception e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				break;
			case EXECUTE:
				try {
					result = execute(device, args);
				} catch (Exception e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				break;
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * render this connected object
	 * @param device
	 * @param args
	 * @return GenericMap
	 * @throws Exception
	 */
	public GenericMap render(DeviceBean device, GenericMap args) throws Exception {
		return renderOrExecute(device, args, true);
	}

	/**
	 * execute action on this connected object
	 * @param device
	 * @param args
	 * @return GenericMap
	 * @throws Exception
	 */
	public GenericMap execute(DeviceBean device, GenericMap args) throws Exception {
		return renderOrExecute(device, args, false);
	}

	/**
	 * genric method for render and execute
	 * @param device
	 * @param args
	 * @param render
	 * @return GenericMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private GenericMap renderOrExecute(DeviceBean device, GenericMap args, boolean render) throws Exception {
		GenericMap result = args;
		/**
		 * read parameters and fix a default value
		 * if parameters is null
		 */
		GenericMap parameters = null;
		if(device.parameters != null) {
			parameters = mapper.readValue(device.parameters, GenericMap.class);
		} else {
			parameters = new GenericMap();
			parameters.put("default", new GenericMap());
		}
		DeviceRest deviceRest = mapperFactory.getMapperFacade().map(device, DeviceRest.class);
		/**
		 * iterate on each entity and execute them as a pipeline
		 */
		for(Entry<String, Object> entry : parameters.entrySet()) {
			GenericMap params = new GenericMap();
			for(Entry<String, Object> param : ((Map<String,Object>) entry.getValue()).entrySet()) {
				params.put(param.getKey(), param.getValue());
			}
			for(GenericEntity entity : sort(apiHrefDeviceScriptPluginResources.findAll(deviceRest, HREF), "order")) {
				ScriptPluginRest script = apiScriptPluginResources.doGetByIdRest(entity.id);
				if(render) {
					logger.info("Before render params = {}, context = {}", params, result);
					result = apiScriptPluginResources.render(device, mapperFactory.getMapperFacade().map(script, ScriptPluginBean.class), params);
					logger.info("After render params = {}, context = {}", params, result);
				} else {
					logger.info("Before execute params = {}, context = {}", params, result);
					result = apiScriptPluginResources.execute(device, mapperFactory.getMapperFacade().map(script, ScriptPluginBean.class), params);
					logger.info("After execute params = {}, context = {}", params, result);
				}
			}
		}
		return result;
	}
}
