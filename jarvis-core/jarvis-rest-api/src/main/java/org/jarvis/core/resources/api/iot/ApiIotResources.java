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

package org.jarvis.core.resources.api.iot;

import static spark.Spark.get;

import java.util.Map;
import java.util.Map.Entry;

import org.jarvis.core.model.bean.iot.IotBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.iot.IotRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.resources.api.ApiLinkedThirdResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.resources.api.href.ApiHrefIotResources;
import org.jarvis.core.resources.api.href.ApiHrefIotScriptPluginResources;
import org.jarvis.core.resources.api.href.ApiHrefIotTriggerResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * IOT resource
 */
@Component
public class ApiIotResources extends ApiLinkedThirdResources<IotRest,IotBean,IotRest,IotBean,ScriptPluginRest,ScriptPluginBean,TriggerRest,TriggerBean> {

	@Autowired
	ApiHrefIotResources apiHrefIotResources;

	@Autowired
	ApiHrefIotScriptPluginResources apiHrefIotScriptPluginResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	@Autowired
	ApiHrefIotTriggerResources apiHrefIotTriggerResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	/**
	 * constructor
	 */
	public ApiIotResources() {
		setRestClass(IotRest.class);
		setBeanClass(IotBean.class);
	}

	@Override
	public void mount() {
		/**
		 * scripts
		 */
		declare(IOT_RESOURCE);
		/**
		 * scripts -> commands
		 */
		declare(IOT_RESOURCE, IOT_RESOURCE, this, apiHrefIotResources, IOT, SORTKEY, HREF);
		declareSecond(IOT_RESOURCE, SCRIPT_RESOURCE, apiScriptPluginResources, apiHrefIotScriptPluginResources, PLUGIN, SORTKEY, HREF);
		declareThird(IOT_RESOURCE, TRIGGER_RESOURCE, apiTriggerResources, apiHrefIotTriggerResources, TRIGGER, SORTKEY, HREF);
		/**
		 * iot html generator
		 */
		get("/api/directives/html/iots/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	IotRest iot = doGetById(request.params(ID));
		    	return iot.template;
		    }
		});
	}

	@Override
	public ResourcePair doRealTask(IotBean iot, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case RENDER:
				result = render(iot, args);
				break;
			case EXECUTE:
				result = execute(iot, args);
				break;
			default:
				result = new GenericMap();
		}
		return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
	}

	/**
	 * render this connected object
	 * @param iot
	 * @param args
	 * @return GenericMap
	 * @throws Exception
	 */
	public GenericMap render(IotBean iot, GenericMap args) throws Exception {
		return renderOrExecute(iot, args, true);
	}

	/**
	 * execute action on this connected object
	 * @param iot
	 * @param args
	 * @return GenericMap
	 * @throws Exception
	 */
	public GenericMap execute(IotBean iot, GenericMap args) throws Exception {
		return renderOrExecute(iot, args, false);
	}

	/**
	 * genric method for render and execute
	 * @param iot
	 * @param args
	 * @param render
	 * @return GenericMap
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private GenericMap renderOrExecute(IotBean iot, GenericMap args, boolean render) throws Exception {
		GenericMap result = args;
		/**
		 * read parameters and fix a default value
		 * if parameters is null
		 */
		GenericMap parameters = null;
		if(iot.parameters != null) {
			parameters = mapper.readValue(iot.parameters, GenericMap.class);
		} else {
			parameters = new GenericMap();
			parameters.put("default", new GenericMap());
		}
		IotRest iotRest = mapperFactory.getMapperFacade().map(iot, IotRest.class);
		/**
		 * iterate on each entity and execute them as a pipeline
		 */
		for(Entry<String, Object> entry : parameters.entrySet()) {
			GenericMap params = new GenericMap();
			for(Entry<String, Object> param : ((Map<String,Object>) entry.getValue()).entrySet()) {
				params.put(param.getKey(), param.getValue());
			}
			for(GenericEntity entity : sort(apiHrefIotScriptPluginResources.findAll(iotRest, HREF), "order")) {
				ScriptPluginRest script = apiScriptPluginResources.doGetById(entity.id);
				if(render) {
					logger.info("Render {}", params);
					result = apiScriptPluginResources.render(script, params);
				} else {
					logger.info("Execute {}", params);
					result = apiScriptPluginResources.execute(script, params);
				}
			}
		}
		return result;
	}
}
