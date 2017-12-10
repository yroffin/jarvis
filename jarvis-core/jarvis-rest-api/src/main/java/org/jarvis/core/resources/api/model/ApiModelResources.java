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

package org.jarvis.core.resources.api.model;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.model.ModelBean;
import org.jarvis.core.model.model.ModelRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

/**
 * device resource
 */
@Api(value = "model")
@Path("/api/models")
@Produces("application/json")
@Component
@Declare(resource = ApiMapper.MODEL_RESOURCE, summary = "Model resource", rest = ModelRest.class)
public class ApiModelResources extends
	ApiResources<ModelRest, ModelBean> {

	/**
	 * constructor
	 */
	public ApiModelResources() {
		setRestClass(ModelRest.class);
		setBeanClass(ModelBean.class);
	}

	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(ModelBean device, GenericMap args, String taskType) throws TechnicalException {
		return new GenericValue(args);
	}
}
