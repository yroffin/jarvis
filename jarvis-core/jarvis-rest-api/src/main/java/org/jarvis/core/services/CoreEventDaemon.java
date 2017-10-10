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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.camunda.bpm.engine.MismatchingMessageCorrelationException;
import org.camunda.bpm.engine.RuntimeService;
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.bean.trigger.TriggerBean;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefDeviceTriggerResources;
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
	CoreStatistics coreStatistics;
	
	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	RuntimeService runtimeService;
	
	private List<MqttTrigger> triggers = new ArrayList<MqttTrigger>();
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
	 * init triggers
	 */
	public void triggers() {
		/**
		 * store spring component in MqttTrigger
		 */
		MqttTrigger.coreEventDaemon = this;

		/**
		 * start mqtt souscription on each trigger (if any mqtt souscription)
		 */
		List<TriggerBean> beans = apiTriggerResources.doFindAllBean();
		for(TriggerBean trigger : beans) {
			if(trigger.topic != null && trigger.topic.length() > 0) {
				MqttTrigger mqTrigger = new MqttTrigger(trigger.id, trigger.name, trigger.topic, trigger.body, env.getProperty("jarvis.mqtt.url"));
				triggers.add(mqTrigger);
				mqTrigger.connect();
			}
		}
		logger.info("Init {} mqtt triggers", triggers.size());
	}
	
	/**
	 * create it
	 * @param trigger
	 */
	public void create(TriggerBean trigger) {
		if(trigger.topic != null && trigger.topic.length() > 0) {
			MqttTrigger mqTrigger = new MqttTrigger(trigger.id, trigger.name, trigger.topic, trigger.body, env.getProperty("jarvis.mqtt.url"));
			triggers.add(mqTrigger);
			mqTrigger.connect();
			logger.info("Create {}", trigger.toString());
		}
	}

	/**
	 * update it
	 * @param bean
	 */
	public void update(TriggerBean bean) {
		MqttTrigger found = null;
		for(MqttTrigger trigger : triggers) {
			if(bean.id.equals(trigger.getId())) {
				found = trigger;
			}
		}
		/**
		 * if found replace it
		 */
		if(found != null) {
			triggers.remove(found);
			found.close();
			if(bean.topic != null && bean.topic.length() > 0) {
				MqttTrigger mqTrigger = new MqttTrigger(bean.id, bean.name, bean.topic, bean.body, env.getProperty("jarvis.mqtt.url"));
				triggers.add(mqTrigger);
				mqTrigger.connect();
				logger.info("Update {}", bean.toString());
			}
		}
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
						/**
						 * start message processus
						 */
						Map<String, Object> variables = new HashMap<String, Object>();
						variables.put("message", event);
						runtimeService.startProcessInstanceByMessage("Message_MQTT", variables);
					} catch (MismatchingMessageCorrelationException e) {
						logger.warn("[EVENT] - MismatchingMessageCorrelationException {}", e);
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
	}
}
