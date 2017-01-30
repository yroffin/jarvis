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

package org.bujo.core.services;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.bujo.core.model.bean.entry.EntryBean;
import org.bujo.core.model.rest.entry.EntryRest;
import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

/**
 * Scenario resource
 *
 */
@Api(value = "entry")
@Path("/api/entries")
@Produces("application/json")
@Component
@Declare(resource=ApiEntryResources.RESOURCE, summary="Scenario resource", rest=ScenarioRest.class)
public class ApiEntryResources extends ApiResources<EntryRest, EntryBean> {
	
	/**
	 * resource name
	 */
	public final static String RESOURCE = "entry";

	@Autowired
	Environment env;

	/**
	 * constructor
	 */
	public ApiEntryResources() {
		setRestClass(EntryRest.class);
		setBeanClass(EntryBean.class);
	}

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		super.init();
	}

	/**
	 * mount resources
	 */
	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(EntryBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		return null;
	}
}
