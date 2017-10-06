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

package org.jarvis.core.services.bpm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.device.EventBean;
import org.jarvis.core.model.bean.process.ProcessBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefDeviceTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefProcessTriggerResources;
import org.jarvis.core.resources.api.process.ApiProcessResources;
import org.jarvis.core.services.CoreStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * BpmTaskMessageService
 */
@Service(value = "BpmTaskMessageService")
public class BpmTaskMessageService implements BpmServiceTask {
	protected Logger logger = LoggerFactory.getLogger(BpmTaskMessageService.class);

	@Autowired
	ApiDeviceResources apiDeviceResources;
	
	@Autowired
	ApiHrefDeviceTriggerResources apiHrefDeviceTriggerResources;

	@Autowired
	CoreStatistics coreStatistics;
	
	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	ApiProcessResources apiProcessResources;

	@Autowired
	ApiHrefProcessTriggerResources apiHrefProcessTriggerResources;
	
	@Override
	public void execute(Map<String, Object> payload) {
		EventBean event = (EventBean) payload.get("message");
		handle(event);
	}

	/**
	 * @param event
	 */
	public void handle(EventBean event) {
		/**
		 * store event in stats
		 */
		coreStatistics.write(event);

		int checkDevice = 0;
		int checkProcess = 0;
		
		/**
		 * execute it
		 */
		for(ProcessBean process : processToExecute(event)) {
			try {
				apiProcessResources.rawExecute(process.id, new GenericMap(), "execute");
				checkProcess++;
			} catch (TechnicalNotFoundException e) {
				logger.warn(e.getMessage());
			}
		}

		/**
		 * execute it
		 */
		for(DeviceBean device : deviceToExecute(event)) {
			try {
				apiDeviceResources.doExecute(device.id, new GenericMap(), "execute");
				checkDevice++;
			} catch (TechnicalNotFoundException e) {
				logger.warn(e.getMessage());
			}
		}

		logger.warn("Trigger activate {} devices and  {} process", checkDevice, checkProcess);
	}

	/**
	 * find process
	 * @param event
	 * @return
	 */
	private List<ProcessBean> processToExecute(EventBean event) {
		List<ProcessBean> processToExecute = new ArrayList<ProcessBean>();
		/**
		 * find any process with this trigger
		 */
		for(ProcessBean process : apiProcessResources.doFindAllBean()) {
			for(GenericEntity link : apiHrefProcessTriggerResources.findAll(process)) {
				try {
					if(event.trigger != null && event.trigger.equals(link.id)) {
						apiTriggerResources.doGetByIdRest(link.id);
						processToExecute.add(process);
					}
				} catch (TechnicalNotFoundException e) {
					logger.warn(e.getMessage());
				}
			}
		}
		return processToExecute;
	}

	/**
	 * find device
	 * @param event
	 * @return
	 */
	private List<DeviceBean> deviceToExecute(EventBean event) {
		List<DeviceBean> deviceToExecute = new ArrayList<DeviceBean>();
		/**
		 * find any device with this trigger
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

