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

package org.jarvis.core.resources.api.href;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.device.DeviceRest;
import org.jarvis.core.model.rest.scenario.TriggerRest;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefDeviceTriggerResources extends ApiHrefMapper<DeviceRest,TriggerRest> {

	@PostConstruct
	protected
	void init() {
		super.init(DeviceBean.class.getSimpleName(),TriggerBean.class.getSimpleName(),"triggers");
	}

	/**
	 * find all by bean
	 * @param bean 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAll(DeviceBean bean) {
		return super.findAll(mapperFactory.getMapperFacade().map(bean, DeviceRest.class), HREF);
	}
}
