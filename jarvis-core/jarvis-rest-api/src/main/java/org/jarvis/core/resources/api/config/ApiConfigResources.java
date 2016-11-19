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

package org.jarvis.core.resources.api.config;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.config.ConfigBean;
import org.jarvis.core.model.rest.config.ConfigRest;
import org.jarvis.core.model.rest.config.ConfigSystemRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourceDefaultPostListenerImpl;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.resources.api.tools.ApiCronResources;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import spark.Request;
import spark.Response;

/**
 * View resource
 */
@Component
@Api(value = "configuration")
@Path("/api/configurations")
@Produces("application/json")
@Declare(resource=ApiMapper.CONFIG_RESOURCE, summary="Configuration resource", rest=ConfigRest.class)
public class ApiConfigResources extends ApiResources<ConfigRest,ConfigBean> {

	@Autowired
	CoreEventDaemon coreEventDaemon;

	@Autowired
	ApiCronResources apiCronResources;
	
	/**
	 * constructor
	 */
	public ApiConfigResources() {
		setRestClass(ConfigRest.class);
		setBeanClass(ConfigBean.class);
	}

	class ResourceListenerImpl extends ResourceDefaultPostListenerImpl<ConfigRest,ConfigBean> implements ResourcePostListener<ConfigRest,ConfigBean> {

		@Override
		public void getRest(Request request, Response response, ConfigRest rest) {
			rest.system = new ConfigSystemRest();
			rest.system.cron = true;
			rest.system.scheduled = apiCronResources.getScheduled();
			rest.system.events = !coreEventDaemon.isInterrupted();
		}
		
	}

	@Override
	public void mount() {
		super.mount();
		
		/**
		 * register listener
		 */
		addPostListener(new ResourceListenerImpl());
	}

	@Override
	public GenericValue doRealTask(ConfigBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		return null;
	}
}
