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
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefDeviceTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioTriggerResources;
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
	ApiDeviceResources apiDeviceResources;
	
	@Autowired
	ApiHrefDeviceTriggerResources apiHrefDeviceTriggerResources;

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
	private LinkedBlockingQueue<EventBean> linked = new LinkedBlockingQueue<>();
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
	 * @param id 
	 * @param name 
	 * @throws InterruptedException 
	 */
	public void post(String id, String name) throws InterruptedException {
		EventBean event = new EventBean();
		event.text = name;
		event.trigger = id;
		try {
			post(event);
		} catch (InterruptedException e) {
			logger.warn("Error {}", e);
			throw e;
		}
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

	/**
	 * is inner thread active ?
	 * @return boolean
	 */
	public boolean isInterrupted() {
		return inner.isInterrupted();
	}

	class InnerThread implements Runnable {

		private boolean interrupted;

		public boolean isInterrupted() {
			return interrupted;
		}

		@Override
		public void run() {
			interrupted = false;
			EventBean event = null;
			try {
				while ((event  = linked.take()) != null) {
					logger.warn("[EVENT] {}/{}", event.toString(), linked.size());
					try {
						handle(event);
					} catch (Exception e) {
						logger.error("[EVENT] - Exception {}", e);
					}
				}
			} catch (InterruptedException e) {
				logger.error("[EVENT] - InterruptedException {}", e);
				interrupted = true;
				Thread.currentThread().interrupt();
			}

			interrupted = true;
			logger.warn("[EVENT] - thread shutdown");
		}

		private void handle(EventBean event) {
			/**
			 * store event in stats
			 */
			coreStatistics.write(event);

			int checkDevice = 0;
			int checkScenario = 0;
			
			/**
			 * execute it
			 */
			for(ScenarioBean scenario : scenarioToExecute(event)) {
				try {
					apiScenarioResources.doExecute(null,scenario.id, new GenericMap(), TaskType.EXECUTE);
					checkScenario++;
				} catch (TechnicalNotFoundException e) {
					logger.warn(e.getMessage());
				}
			}

			/**
			 * execute it
			 */
			for(DeviceBean device : deviceToExecute(event)) {
				try {
					apiDeviceResources.doExecute(device.id, new GenericMap(), TaskType.EXECUTE);
					checkDevice++;
				} catch (TechnicalNotFoundException e) {
					logger.warn(e.getMessage());
				}
			}

			logger.info("Trigger activate {} devices and  {} scenario", checkDevice, checkScenario);
		}

		/**
		 * find scenario
		 * @param event
		 * @return
		 */
		private List<ScenarioBean> scenarioToExecute(EventBean event) {
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
			return scenarioToExecute;
		}

		/**
		 * find device
		 * @param event
		 * @return
		 */
		private List<DeviceBean> deviceToExecute(EventBean event) {
			List<DeviceBean> deviceToExecute = new ArrayList<DeviceBean>();
			/**
			 * find any scenario with this trigger
			 */
			for(DeviceBean deviceBean : apiDeviceResources.doFindAllBean()) {
				for(GenericEntity link : apiHrefDeviceTriggerResources.findAll(deviceBean)) {
					try {
						if(event.trigger != null && event.trigger.equals(link.id)) {
							apiTriggerResources.doGetByIdRest(link.id);
							deviceToExecute.add(deviceBean);
						}
					} catch (TechnicalNotFoundException e) {
						logger.warn(e.getMessage());
					}
				}
			}
			return deviceToExecute;
		}
		
	}
}
