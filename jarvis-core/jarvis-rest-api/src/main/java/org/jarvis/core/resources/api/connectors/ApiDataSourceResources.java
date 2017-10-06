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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalHttpException;
import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.DataSourceBean;
import org.jarvis.core.model.bean.connector.DataSourcePipeBean;
import org.jarvis.core.model.bean.connector.DataSourceQueryBean;
import org.jarvis.core.model.bean.connector.MeasureBean;
import org.jarvis.core.model.bean.connector.MeasureDataBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.model.rest.connector.DataSourceRest;
import org.jarvis.core.model.rest.connector.MeasureRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefConnectorResources;
import org.jarvis.core.resources.api.href.ApiHrefMeasureResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.common.core.type.GenericMap;
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
	@DeclareLinkedResource(role=ApiMapper.MEASURE_RESOURCE, param=ApiMapper.MEASURE, sortKey=ApiMapper.SORTKEY)
	public ApiMeasureResources apiMeasureResources;

	/**
	 * href handle
	 */
	@Autowired
	@DeclareHrefResource(role=ApiMapper.MEASURE_RESOURCE, href=ApiMapper.HREF, target=MeasureRest.class)
	public ApiHrefMeasureResources apiHrefMeasureResources;
	
	/**
	 * connector resource
	 */
	@Autowired
	public ApiConnectorResources apiConnectorResources;

	/**
	 * href handle
	 */
	@Autowired
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
	public GenericValue doRealTask(DataSourceBean bean, GenericMap args, String taskType) {
		GenericMap result;
		switch(taskType) {
			case "execute":
				return execute(bean, args);
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * execute this datasource to render all connector
	 * @param bean
	 * @param args
	 * @return
	 */
	private GenericValue execute(DataSourceBean bean, GenericMap args) {
		/**
		 * find all mesures on this datasource
		 */
		List<GenericEntity> links = apiHrefMeasureResources.findAll(bean, ApiMapper.HREF);
		for(GenericEntity link: links) {
			try {
				MeasureBean measure = apiMeasureResources.doGetByIdBean(link.id);
				try {
					Boolean delta = true;
					DateTime minDate = DateTime.now().minusHours(1);
					DateTime maxDate = DateTime.now();
					Integer truncate = 16;

					// override values
					if(args.containsKey("minDate")) {
						minDate = DateTime.parse((String) args.get("minDate"));
					}
					if(args.containsKey("maxDate")) {
						maxDate = DateTime.parse((String) args.get("maxDate"));
					}
					if(args.containsKey("truncate")) {
						truncate = (Integer) args.get("truncate");
					}
					if(args.containsKey("delta")) {
						delta = (Boolean) args.get("delta");
					}
					if(args.containsKey("minusDays")) {
						minDate = minDate.minusDays((int) args.get("minusDays"));
					}
					if(args.containsKey("minusMinutes")) {
						minDate = minDate.minusMinutes((int) args.get("minusMinutes"));
					}
					if(args.containsKey("minusSeconds")) {
						minDate = minDate.minusSeconds((int) args.get("minusSeconds"));
					}
					
					List<GenericEntity> connectors = apiHrefConnectorResources.findAll(measure, ApiMapper.HREF);
					ConnectorRest connector = apiConnectorResources.doGetByIdRest(connectors.get(0).id);
					// build datasource
					DataSourceQueryBean query = new DataSourceQueryBean();
					// project
					DataSourcePipeBean project = new DataSourcePipeBean();
					project.project = new HashMap<String, Object>();
					Map<String, Object> hash = new HashMap<String, Object>();
					List<Object> substr = new ArrayList<Object>();
					project.project.put(measure.datetime,1);
					project.project.put(measure.value,1);
					substr.add("$"+measure.datetime);
					substr.add(0);
					substr.add(truncate);
					hash.put("$substr", substr);
					project.project.put("hash",hash);
					// match
					DataSourcePipeBean match = new DataSourcePipeBean();
					match.match = new HashMap<String, Object>();
					Map<String, Object> matchValue = new HashMap<String, Object>();
					matchValue.put("$gte", "ISODate("+minDate+")");
					matchValue.put("$lte", "ISODate("+maxDate+")");
					match.match.put(measure.datetime, matchValue);
					// sort
					DataSourcePipeBean sort = new DataSourcePipeBean();
					sort.sort = new HashMap<String, Object>();
					sort.sort.put(measure.datetime, -1);
					// group
					DataSourcePipeBean group = new DataSourcePipeBean();
					group.group = new HashMap<String, Object>();
					Map<String, Object> idMap = new HashMap<String, Object>();
					idMap.put("label", "$hash");
					group.group.put("_id", idMap);
					Map<String, Object> idAvg = new HashMap<String, Object>();
					idAvg.put("$avg", "$"+measure.value);
					group.group.put("avg", idAvg);
					Map<String, Object> idMax = new HashMap<String, Object>();
					idMax.put("$max", "$"+measure.value);
					group.group.put("max", idMax);
					Map<String, Object> idMin = new HashMap<String, Object>();
					idMin.put("$min", "$"+measure.value);
					group.group.put("min", idMin);
					// build pipes
					query.pipes.add(project);
					query.pipes.add(match);
					query.pipes.add(sort);
					query.pipes.add(group);
					//System.err.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(query));
					List<GenericMap> res = apiConnectorResources.pipes(connector, measure.name, mapper.writeValueAsString(query));
					// compute delta on each value
					List<MeasureDataBean> model = new ArrayList<MeasureDataBean>();
					// add raw value
					for(GenericMap value : res) {
						model.add(new MeasureDataBean(
								(String) ((LinkedHashMap<?,?>) value.get("_id")).get("label"),
								convertToDouble(value.get("max")),
								convertToDouble(value.get("min")),
								convertToDouble(value.get("avg"))
								));
					}
					// sort it (mongodb sort is strange)
					Collections.sort(model, new Comparator<MeasureDataBean>() {

						@Override
						public int compare(MeasureDataBean i1, MeasureDataBean i2) {
							return i1.label.compareTo(i2.label);
						}
						
					});
					// delta
					if(delta) {
						MeasureDataBean reference = null;
						for(MeasureDataBean value : model) {
							if(reference == null) {
								reference = value;
								reference.reset();
							} else {
								value.delta(reference);
								reference = value;
							}
						}
					}
					return new GenericValue(mapper.writeValueAsString(model));
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
	 * short convert
	 * @param value
	 * @return
	 */
	private static double convertToDouble(Object value) {
		if(value instanceof Double) {
			return (double) value;
		}
		if(value instanceof Integer) {
			return (double) ((Integer) value);
		}
		return 0;
	}
}
