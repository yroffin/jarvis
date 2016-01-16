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
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockScriptPluginResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Block resource
 *
 */
@Component
public class ApiBlockResources extends ApiLinkedResources<BlockRest,BlockBean,ScriptPluginRest,ScriptPluginBean> {

	@Autowired
	ApiHrefBlockScriptPluginResources apiHrefBlockScriptPluginResources;
	
	@Autowired
	ApiHrefBlockBlockResources ApiHrefBlockBlockResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

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
		 * scenarios
		 */
		declare(BLOCK_RESOURCE);
		/**
		 * blocks->plugins
		 */
		declare(BLOCK_RESOURCE, SCRIPT_RESOURCE, apiScriptPluginResources, apiHrefBlockScriptPluginResources, PLUGIN, SORTKEY);
	}

	@Override
	public String doRealTask(BlockBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case TEST:
				return test(bean, args, new GenericMap())+"";
			case EXECUTE:
				return execute(bean, args)+"";
			default:
				result = new GenericMap();
				return mapper.writeValueAsString(result);
		}
	}

	@Autowired
	PluginGroovyService pluginGroovyService;

	private boolean test(BlockBean bean, GenericMap args, GenericMap genericMap) throws TechnicalNotFoundException {
		boolean result = false;
		if(bean.pluginId != null) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(bean.pluginId, args, TaskType.EXECUTE);
			return pluginGroovyService.groovyAsBoolean(bean.expression, exec);
		}
		return result;
	}

	/**
	 * @param bean
	 * @param args
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public GenericMap execute(BlockBean bean, GenericMap args) throws TechnicalNotFoundException {
		GenericMap result = new GenericMap();
		if(bean.pluginId != null) {
			GenericMap exec = (GenericMap) apiScriptPluginResources.doExecute(bean.pluginId, args, TaskType.EXECUTE);
			if(pluginGroovyService.groovyAsBoolean(bean.expression, exec)) {
				if(bean.pluginThenId != null) {
					GenericMap thn = (GenericMap) apiScriptPluginResources.doExecute(bean.pluginThenId, args, TaskType.EXECUTE);
				}
			} else {
				if(bean.pluginElseId != null) {
					GenericMap els = (GenericMap) apiScriptPluginResources.doExecute(bean.pluginElseId, args, TaskType.EXECUTE);
				}
			}
		}
		return result;
	}
}
