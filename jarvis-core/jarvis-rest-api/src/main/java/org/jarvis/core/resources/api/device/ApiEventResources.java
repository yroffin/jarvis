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

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.rest.device.EventRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourceDefaultPostListenerImpl;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import spark.Request;
import spark.Response;

/**
 * Event resource
 */
@Api(value = "event")
@Path("/api/events")
@Produces("application/json")
@Component
@Declare(resource=ApiMapper.EVENT_RESOURCE, summary="Event resource", rest=EventRest.class)
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

	class ResourceListenerImpl extends ResourceDefaultPostListenerImpl<EventRest, EventBean> implements ResourcePostListener<EventRest, EventBean> {

		@Override
		public void postRest(Request request, Response response, EventRest event) {
			if(request.params(ASYNC) != null && request.params(ASYNC).equals("true")) {
				try {
					coreEventDaemon.post(mapRestToBean(event));
				} catch (InterruptedException e) {
					throw new TechnicalException(e);
				}
			} else {
				coreEventDaemon.handle(mapRestToBean(event));
			}
		}

	}

	@Override
	public void mount() {
		super.mount();
		/**
		 * declare listener
		 */
		addPostListener(new ResourceListenerImpl());
	}

	@Override
	public GenericValue doRealTask(EventBean Event, GenericMap args, TaskType taskType) throws TechnicalException {
		throw new TechnicalException("Not implemented");
	}
}
