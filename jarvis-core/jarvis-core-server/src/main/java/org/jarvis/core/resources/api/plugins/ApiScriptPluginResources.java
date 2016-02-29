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

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.resources.api.href.ApiHrefPluginCommandResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * script plugin resource
 */
@Component
public class ApiScriptPluginResources extends ApiLinkedResources<ScriptPluginRest,ScriptPluginBean,CommandRest,CommandBean> {

	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiHrefPluginCommandResources apiHrefPluginCommandResources;

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
		 * scripts
		 */
		declare(SCRIPT_RESOURCE);
		/**
		 * scripts -> commands
		 */
		declare(SCRIPT_RESOURCE, COMMAND_RESOURCE, apiCommandResources, apiHrefPluginCommandResources, COMMAND, SORTKEY, HREF);
	}

	@Override
	public ResourcePair doRealTask(ScriptPluginBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case RENDER:
				result = renderOrExecute(bean, args, new GenericMap(), true);
				break;
			case EXECUTE:
				result = renderOrExecute(bean, args, new GenericMap(), false);
				break;
			default:
				result = new GenericMap();
		}
		return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
	}

	/**
	 * render all data command of this script as a pipeline
	 * @param script
	 * 		the script to render
	 * @param args
	 * 		arguments for this script
	 * @return GenericMap
	 * 		a result as a generic map
	 * @throws TechnicalNotFoundException
	 * 		if not found 
	 */
	public GenericMap render(ScriptPluginRest script, GenericMap args) throws TechnicalNotFoundException {
		return renderOrExecute(mapperFactory.getMapperFacade().map(script, ScriptPluginBean.class), args, new GenericMap(), true);
	}

	/**
	 * execute all action command of this script as a pipeline
	 * @param script
	 * 		the script to render
	 * @param args
	 * 		arguments for this script
	 * @return GenericMap
	 * 		a result as a generic map
	 * @throws TechnicalNotFoundException
	 * 		if not found 
	 */
	public GenericMap execute(ScriptPluginRest script, GenericMap args) throws TechnicalNotFoundException {
		return renderOrExecute(mapperFactory.getMapperFacade().map(script, ScriptPluginBean.class), args, new GenericMap(), false);
	}

	/**
	 * execute all command of this script as a pipeline
	 * @param script
	 * @param args 
	 * @param output 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	private GenericMap renderOrExecute(ScriptPluginBean script, GenericMap args, GenericMap output, boolean render) throws TechnicalNotFoundException {
		GenericMap result = args;
		int index = 0;
		for(GenericEntity entity : sort(apiHrefPluginCommandResources.findAll(script), "order")) {
			/**
			 * retrieve command to execute
			 */
			CommandRest command = apiCommandResources.doGetById(entity.id);
			/**
			 * in render mode only execute data
			 */
			if(entity.get("type") != null && entity.get("type").equals("data") && render) {
				logger.info("Render {}", command);
				result = apiCommandResources.execute(mapperFactory.getMapperFacade().map(command, CommandBean.class), result);
			}
			/**
			 * in execute mode only execute action
			 */
			if(entity.get("type") != null && entity.get("type").equals("action") && !render) {
				logger.info("Execute {}", command);
				result = apiCommandResources.execute(mapperFactory.getMapperFacade().map(command, CommandBean.class), result);
			}
			/**
			 * store result in output
			 */
			if(entity.get("name") != null) {
				output.put((String) entity.get("name"), result);
			} else {
				output.put("field"+(index++), result);
			}
		}
		return output;
	}
}
