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
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.rest.device.EventRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;

/**
 * Event resource
 */
@Component
public class ApiEventResources extends ApiResources<EventRest,EventBean> {

	@Autowired
	CoreEventDaemon coreEventDaemon;
	
	/**
	 * constructor
	 */
	public ApiEventResources() {
		setRestClass(EventRest.class);
		setBeanClass(EventBean.class);
	}

	class ResourceListenerImpl implements ResourcePostListener<EventBean> {

		@Override
		public void post(Request request, Response response, EventBean event) throws InterruptedException {
			if(request.params(ASYNC) != null && request.params(ASYNC).equals("true")) {
				coreEventDaemon.post(event);
			} else {
				coreEventDaemon.handle(event);
			}
		}

		@Override
		public void put(Request request, Response response, EventBean t) {
		}
	}

	@Override
	public void mount() {
		/**
		 * events
		 */
		declare(EVENT_RESOURCE);
		/**
		 * declare listener
		 */
		addListener(new ResourceListenerImpl());
	}

	@Override
	public GenericValue doRealTask(EventBean Event, GenericMap args, TaskType taskType) throws TechnicalException {
		return null;
	}
}
