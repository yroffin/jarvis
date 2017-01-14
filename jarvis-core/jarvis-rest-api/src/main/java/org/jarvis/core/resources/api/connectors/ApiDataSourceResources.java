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

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.DataSourceBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.model.rest.connector.DataSourceRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefConnectorResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;

/**
 * View resource
 */
@Component
@Api(value = "datasource")
@Path("/api/datasources")
@Produces("application/json")
@Declare(resource=ApiMapper.DATASOURCE_RESOURCE, summary="DataSource resource", rest=DataSourceRest.class)
public class ApiDataSourceResources extends ApiLinkedResources<DataSourceRest,DataSourceBean,ConnectorRest,ConnectorBean> {

	/**
	 * link to another device
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
		GenericMap result;
		switch(taskType) {
			case INDICATOR:
				try {
					result = indicators(bean, args);
				} catch (Exception e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
				break;
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * retrieve indicators
	 * @param bean
	 * @param args
	 * @return
	 */
	private GenericMap indicators(DataSourceBean bean, GenericMap args) {
		GenericMap result = new GenericMap();
		List<GenericEntity> links = apiHrefConnectorResources.findAll(bean, ApiMapper.HREF);
		for(GenericEntity link: links) {
			try {
				ConnectorRest rest = apiConnectorResources.doGetByIdRest(link.id);
				try {
					rest.collects = apiConnectorResources.findCollectors(rest);
				} catch (TechnicalHttpException e) {
					throw new TechnicalException(e);
				}
				result.put("adress", rest.adress + "/api/collect");
				result.put("data", rest.collects.get("collections"));
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		}
		return result;
	}
}
