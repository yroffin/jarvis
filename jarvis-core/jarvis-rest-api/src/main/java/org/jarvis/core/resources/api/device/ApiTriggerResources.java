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

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefTriggerCronResources;
import org.jarvis.core.resources.api.tools.ApiCronResources;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Trigger resource
 */
@Component
public class ApiTriggerResources extends ApiLinkedResources<TriggerRest,TriggerBean,CronRest,CronBean> {

	@Autowired
	ApiCronResources apiCronResources;
	
	@Autowired
	ApiHrefTriggerCronResources apiHrefTriggerCronResources;

	@Autowired
	CoreEventDaemon coreEventDaemon;

	/**
	 * constructor
	 */
	public ApiTriggerResources() {
		setRestClass(TriggerRest.class);
		setBeanClass(TriggerBean.class);
	}

	@Override
	public void mount() {
		/**
		 * scripts
		 */
		declare(TRIGGER_RESOURCE);
		/**
		 * trigger -> cron
		 */
		declare(TRIGGER_RESOURCE, CRON_RESOURCE, apiCronResources, apiHrefTriggerCronResources, CRON, SORTKEY, HREF);
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
