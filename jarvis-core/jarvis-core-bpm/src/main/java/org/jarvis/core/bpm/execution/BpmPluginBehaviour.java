/**
 *  Copyright 2017 Yannick Roffin
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

package org.jarvis.core.bpm.execution;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;
import org.common.core.type.GenericMap;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * BpmServiceTaskBehaviour
 */
public class BpmPluginBehaviour implements JavaDelegate {

	protected Logger logger = LoggerFactory.getLogger(BpmPluginBehaviour.class);

	private static ConfigurableApplicationContext context;
	
	/**
	 * id field
	 */
	public FixedValue id;
	
	/**
	 * name field
	 */
	public FixedValue name;

	/**
	 * internal spring service
	 */
	private ApiScriptPluginResources apiScriptPluginResources;

	/**
	 * constructor
	 */
	public BpmPluginBehaviour() {
		apiScriptPluginResources = BpmPluginBehaviour.context.getBean(ApiScriptPluginResources.class);
	}

	/**
	 * @param ctx
	 */
	public static void setContext(ConfigurableApplicationContext ctx) {
		BpmPluginBehaviour.context = ctx;
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		/**
		 * get payload
		 */
		Map<String, Object> payload = new HashMap<String, Object>(execution.getVariables());

		/**
		 * find this bean
		 */
		try {
			if(id == null) {
				logger.warn("While executing service task {} id is null", execution.getCurrentActivityId());
				return;
			}
			/**
			 * execute this script
			 */
			GenericMap parameters = new GenericMap();
			GenericValue exec = apiScriptPluginResources.rawExecute(id.getExpressionText(), parameters, "execute");
			switch(exec.getType()) {
				case BOOLEAN:
					execution.setVariable("output", exec.asBoolean());
					break;
				case OBJECT:
					execution.setVariable("output", exec.asObject());
					break;
				case STRING:
					execution.setVariable("output", exec.asString());
					break;
				case ARRAY:
					execution.setVariable("output", exec.asList());
					break;
				default:
					execution.setVariable("output", false);
					break;
			}
			logger.warn("[EXECUTE] {} {}", payload, execution.getVariables());
		} catch (NoSuchBeanDefinitionException e) {
			logger.warn("[BEAN] no such bean {}", "Bpm" + execution.getCurrentActivityId() + "Service");		
		}
	}
}
