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
import org.jarvis.core.services.bpm.BpmServiceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * BpmServiceTaskBehaviour
 */
public class BpmServiceTaskBehaviour implements JavaDelegate {

	protected Logger logger = LoggerFactory.getLogger(BpmServiceTaskBehaviour.class);

	private static ConfigurableApplicationContext context;

	/**
	 * @param ctx
	 */
	public static void setContext(ConfigurableApplicationContext ctx) {
		BpmServiceTaskBehaviour.context = ctx;
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
			String bean = "Bpm" + execution.getCurrentActivityId() + "Service";
			BpmServiceTask beanService = BpmServiceTaskBehaviour.context.getBean(bean, BpmServiceTask.class);
			logger.warn("[PAYLOAD] {} {}", bean, payload);
			beanService.execute(payload);
		} catch (NoSuchBeanDefinitionException e) {
			logger.warn("[BEAN] no such bean {}", "Bpm" + execution.getCurrentActivityId() + "Service");		
		}
	}
}
