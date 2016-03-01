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

package org.jarvis.core.resources.api.connectors;

import org.jarvis.core.model.bean.connector.ConnexionBean;
import org.jarvis.core.model.rest.connector.ConnexionRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

/**
 * View resource
 */
@Component
public class ApiConnexionResources extends ApiResources<ConnexionRest,ConnexionBean> {

	/**
	 * constructor
	 */
	public ApiConnexionResources() {
		setRestClass(ConnexionRest.class);
		setBeanClass(ConnexionBean.class);
	}

	@Override
	public void mount() {
		/**
		 * connectors
		 */
		declare(CONNEXION_RESOURCE);
	}

	@Override
	public ResourcePair doRealTask(ConnexionBean bean, GenericMap args, TaskType taskType) throws Exception {
		return null;
	}
}
