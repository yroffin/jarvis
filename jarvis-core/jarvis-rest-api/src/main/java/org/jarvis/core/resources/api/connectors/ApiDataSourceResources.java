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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.DataSourceBean;
import org.jarvis.core.model.bean.connector.DataSourcePipeBean;
import org.jarvis.core.model.bean.connector.DataSourceQueryBean;
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
import org.joda.time.DateTime;
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
			case RENDER:
				try {
					return render(bean, args);
				} catch (Exception e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			case EXECUTE:
				try {
					return execute(bean, args);
				} catch (Exception e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
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

	/**
	 * render indicators
	 * @param bean
	 * @param args
	 * @return
	 */
	private GenericValue render(DataSourceBean bean, GenericMap args) {
		List<GenericEntity> links = apiHrefConnectorResources.findAll(bean, ApiMapper.HREF);
		for(GenericEntity link: links) {
			try {
				ConnectorRest rest = apiConnectorResources.doGetByIdRest(link.id);
				try {
					GenericMap collects = apiConnectorResources.findCollectors(rest);
					if(collects.get("collections") != null ) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						List<LinkedHashMap> indicators = (List<LinkedHashMap>) collects.get("collections");
						if(indicators.get(0).get("name") != null) {
							List<GenericMap> res = apiConnectorResources.pipes(rest, (String) indicators.get(0).get("name"), mapper.writeValueAsString(args.get("query")));
							return new GenericValue(mapper.writeValueAsString(res));
						}
					}
					throw new TechnicalException("Unable to find any collections");
				} catch (TechnicalHttpException e) {
					throw new TechnicalException(e);
				} catch (JsonProcessingException e) {
					throw new TechnicalException(e);
				}
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		}
		return new GenericValue("[]");
	}

	/**
	 * execute this datasource to render all connector
	 * @param bean
	 * @param args
	 * @return
	 */
	private GenericValue execute(DataSourceBean bean, GenericMap args) {
		List<GenericEntity> links = apiHrefConnectorResources.findAll(bean, ApiMapper.HREF);
		for(GenericEntity link: links) {
			try {
				ConnectorRest rest = apiConnectorResources.doGetByIdRest(link.id);
				try {
					GenericMap collects = apiConnectorResources.findCollectors(rest);
					if(collects.get("collections") != null ) {
						@SuppressWarnings({ "unchecked", "rawtypes" })
						List<LinkedHashMap> indicators = (List<LinkedHashMap>) collects.get("collections");
						if(indicators.get(0).get("name") != null) {
							String timestamp = "timestamp";
							String value = "base";
							DataSourceQueryBean query = new DataSourceQueryBean();
							// project
							DataSourcePipeBean project = new DataSourcePipeBean();
							project.project = new HashMap<String, Object>();
							Map<String, Object> hash = new HashMap<String, Object>();
							List<Object> substr = new ArrayList<Object>();
							project.project.put(timestamp,1);
							project.project.put(value,1);
							substr.add("$"+timestamp);
							substr.add(0);
							substr.add(args.get("truncate"));
							hash.put("$substr", substr);
							project.project.put("hash",hash);
							// match
							DataSourcePipeBean match = new DataSourcePipeBean();
							match.match = new HashMap<String, Object>();
							Map<String, Object> matchValue = new HashMap<String, Object>();
							matchValue.put("$gte", "ISODate("+args.get("minDate")+")");
							matchValue.put("$lte", "ISODate("+args.get("maxDate")+")");
							match.match.put(timestamp, matchValue);
							// sort
							DataSourcePipeBean sort = new DataSourcePipeBean();
							sort.sort = new HashMap<String, Object>();
							sort.sort.put(timestamp, -1);
							// group
							DataSourcePipeBean group = new DataSourcePipeBean();
							group.group = new HashMap<String, Object>();
							Map<String, Object> idMap = new HashMap<String, Object>();
							idMap.put("label", "$hash");
							group.group.put("_id", idMap);
							Map<String, Object> idAvg = new HashMap<String, Object>();
							idAvg.put("$avg", "$"+value);
							group.group.put("avg", idAvg);
							Map<String, Object> idMax = new HashMap<String, Object>();
							idMax.put("$max", "$"+value);
							group.group.put("max", idMax);
							Map<String, Object> idMin = new HashMap<String, Object>();
							idMin.put("$min", "$"+value);
							group.group.put("min", idMin);
							// build pipes
							query.pipes.add(project);
							query.pipes.add(match);
							query.pipes.add(sort);
							query.pipes.add(group);
							//System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(query));
							List<GenericMap> res = apiConnectorResources.pipes(rest, (String) indicators.get(0).get("name"), mapper.writeValueAsString(query));
							return new GenericValue(mapper.writeValueAsString(res));
						}
					}
					throw new TechnicalException("Unable to find any collections");
				} catch (TechnicalHttpException e) {
					throw new TechnicalException(e);
				} catch (JsonProcessingException e) {
					throw new TechnicalException(e);
				}
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		}
		return new GenericValue("[]");
	}
}
