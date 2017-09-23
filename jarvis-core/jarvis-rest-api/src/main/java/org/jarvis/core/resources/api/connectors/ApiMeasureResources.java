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

import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.MeasureBean;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.model.rest.connector.MeasureRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefConnectorResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.common.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;

/**
 * Measure resource
 */
@Component
@Api(value = "measure")
@Path("/api/measures")
@Produces("application/json")
@Declare(resource=ApiMapper.MEASURE_RESOURCE, summary="Measure resource", rest=MeasureRest.class)
public class ApiMeasureResources extends ApiLinkedResources<MeasureRest,MeasureBean,ConnectorRest,ConnectorBean> {

	/**
	 * link
	 */
	@Autowired
	@DeclareLinkedResource(role=ApiMapper.CONNECTOR_RESOURCE, param=ApiMapper.CONNECTOR, sortKey=ApiMapper.SORTKEY)
	public ApiConnectorResources apiConnectorResources;

	/**
	 * href handle
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.CONNECTOR_RESOURCE, href=ApiMapper.HREF, target=ConnectorRest.class)
	public ApiHrefConnectorResources apiHrefConnectorResources;

	/**
	 * constructor
	 */
	public ApiMeasureResources() {
		setRestClass(MeasureRest.class);
		setBeanClass(MeasureBean.class);
	}

	@Override
	public void mount() {
		super.mount();
	}

	@Override
	public GenericValue doRealTask(MeasureBean bean, GenericMap args, TaskType taskType) {
		GenericMap result = new GenericMap();
		return new GenericValue(result);
	}
}
