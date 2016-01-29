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

import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.rest.scenario.ScenarioRest;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.scenario.ApiBlockResources;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
@PropertySource("classpath:server.properties")
public class CoreEventDaemon {
	
	protected Logger logger = LoggerFactory.getLogger(CoreEventDaemon.class);

	@Autowired
	Environment env;
	
	@Autowired
	ApiScenarioResources apiScenarioResources;

	@Autowired
	ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;

	@Autowired
	ApiBlockResources apiBlockResources;

	/**
	 * start component
	 */
	@PostConstruct
	public void init() {
		new Thread(new InnerThread()).start();
	}
	
	/**
	 * @param event
	 */
	public void post(EventBean event) {
		try {
			linked.put(event);
		} catch (InterruptedException e) {
			throw new TechnicalException(e);
		}
	}

	private LinkedBlockingQueue<EventBean> linked = new LinkedBlockingQueue<EventBean>();

	class InnerThread implements Runnable {

		@Override
		public void run() {
			EventBean event = null;
			try {
				while ((event  = linked.take()) != null) {
					logger.error("[EVENT] {}", event.toString());
					handle(event);
				}
			} catch (InterruptedException e) {
				logger.error("{}", e);
			}
		}

		private void handle(EventBean event) {
			/**
			 * find any scenario with this trigger
			 */
			for(ScenarioRest sce : apiScenarioResources.doFindAll()) {
			}
		}
		
	}
}
