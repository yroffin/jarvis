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

package org.jarvis.core.resources.api.views;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.view.ViewBean;
import org.jarvis.core.model.rest.device.DeviceRest;
import org.jarvis.core.model.rest.view.ViewRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.href.ApiHrefViewResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * View resource
 */
@Component
public class ApiViewResources extends ApiLinkedResources<ViewRest,ViewBean,DeviceRest,DeviceBean> {

	@Autowired
	ApiDeviceResources apiDeviceResources;

	@Autowired
	ApiHrefViewResources apiHrefViewResources;

	/**
	 * constructor
	 */
	public ApiViewResources() {
		setRestClass(ViewRest.class);
		setBeanClass(ViewBean.class);
	}

	@Override
	public void mount() {
		/**
		 * views
		 */
		declare(VIEW_RESOURCE);
		/**
		 * views -> devices
		 */
		declare(VIEW_RESOURCE, DEVICE_RESOURCE, apiDeviceResources, apiHrefViewResources, DEVICE, SORTKEY, HREF);
	}

	@Override
	public GenericValue doRealTask(ViewBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		return null;
	}
}
