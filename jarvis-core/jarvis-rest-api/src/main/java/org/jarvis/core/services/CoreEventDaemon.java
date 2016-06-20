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

package org.jarvis.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.bean.iot.IotBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.href.ApiHrefIotTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioTriggerResources;
import org.jarvis.core.resources.api.iot.ApiIotResources;
import org.jarvis.core.resources.api.iot.ApiTriggerResources;
import org.jarvis.core.resources.api.scenario.ApiBlockResources;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * main daemon
 */
@Component
public class CoreEventDaemon {
	
	protected Logger logger = LoggerFactory.getLogger(CoreEventDaemon.class);

	@Autowired
	Environment env;
	
	@Autowired
	ApiIotResources apiIotResources;
	
	@Autowired
	ApiHrefIotTriggerResources apiHrefIotTriggerResources;

	@Autowired
	ApiScenarioResources apiScenarioResources;

	@Autowired
	ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;

	@Autowired
	ApiBlockResources apiBlockResources;

	@Autowired
	ApiHrefScenarioTriggerResources apiHrefScenarioTriggerResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	CoreStatistics coreStatistics;
	
	private InnerThread inner;
	protected MapperFactory mapperFactory = null;

	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		
		inner = new InnerThread();
		new Thread(inner).start();
	}
	
	/**
	 * @param event
	 * @throws InterruptedException 
	 */
	public void post(EventBean event) throws InterruptedException {
		try {
			linked.put(event);
		} catch (InterruptedException e) {
			logger.warn("Exception {}", e);
			throw e;
		}
	}

	/**
	 * @param event
	 */
	public void handle(EventBean event) {
		inner.handle(event);
	}

	private LinkedBlockingQueue<EventBean> linked = new LinkedBlockingQueue<EventBean>();

	class InnerThread implements Runnable {

		@Override
		public void run() {
			
			EventBean event = null;
			try {
				while ((event  = linked.take()) != null) {
					logger.warn("[EVENT] {}/{}", event.toString(), linked.size());
					handle(event);
				}
			} catch (InterruptedException e) {
				logger.error("{}", e);
			}
		}

		private void handle(EventBean event) {
			/**
			 * store event in stats
			 */
			coreStatistics.write(event);

			List<ScenarioBean> scenarioToExecute = new ArrayList<ScenarioBean>();
			/**
			 * find any scenario with this trigger
			 */
			for(ScenarioBean sce : apiScenarioResources.doFindAllBean()) {
				for(GenericEntity link : apiHrefScenarioTriggerResources.findAll(sce)) {
					try {
						if(event.trigger != null && event.trigger.equals(link.id)) {
							apiTriggerResources.doGetByIdRest(link.id);
							scenarioToExecute.add(sce);
						}
					} catch (TechnicalNotFoundException e) {
						logger.warn(e.getMessage());
					}
				}
			}

			/**
			 * execute it
			 */
			GenericMap body = new GenericMap();
			for(ScenarioBean scenario : scenarioToExecute) {
				try {
					apiScenarioResources.doExecute(null,scenario.id, body, TaskType.EXECUTE);
				} catch (TechnicalNotFoundException e) {
					logger.warn(e.getMessage());
				}
			}

			List<IotBean> iotToExecute = new ArrayList<IotBean>();
			/**
			 * find any scenario with this trigger
			 */
			for(IotBean iotBean : apiIotResources.doFindAllBean()) {
				for(GenericEntity link : apiHrefIotTriggerResources.findAll(iotBean)) {
					try {
						if(event.trigger != null && event.trigger.equals(link.id)) {
							apiTriggerResources.doGetByIdRest(link.id);
							iotToExecute.add(iotBean);
						}
					} catch (TechnicalNotFoundException e) {
						logger.warn(e.getMessage());
					}
				}
			}

			/**
			 * execute it
			 */
			for(IotBean iot : iotToExecute) {
				try {
					apiIotResources.doExecute(null,iot.id, body, TaskType.EXECUTE);
				} catch (TechnicalNotFoundException e) {
					logger.warn(e.getMessage());
				}
			}
		}
		
	}
}
