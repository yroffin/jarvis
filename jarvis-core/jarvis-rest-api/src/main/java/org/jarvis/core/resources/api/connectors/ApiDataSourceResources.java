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

package org.jarvis.core.resources.api.connectors;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.model.bean.connector.DataSourceBean;
import org.jarvis.core.model.rest.connector.DataSourceRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

/**
 * View resource
 */
@Component
@Api(value = "datasource")
@Path("/api/datasources")
@Produces("application/json")
@Declare(resource=ApiMapper.DATASOURCE_RESOURCE, summary="DataSource resource", rest=DataSourceRest.class)
public class ApiDataSourceResources extends ApiResources<DataSourceRest,DataSourceBean> {

	/**
	 * constructor
	 */
	public ApiDataSourceResources() {
		setRestClass(DataSourceRest.class);
		setBeanClass(DataSourceBean.class);
	}

	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(DataSourceBean bean, GenericMap args, TaskType taskType) {
		return null;
	}
}
