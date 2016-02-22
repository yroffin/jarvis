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

import java.util.List;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.ConnexionBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.model.rest.connector.ConnexionRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.resources.api.href.ApiHrefConnectorResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * View resource
 */
@Component
public class ApiConnectorResources extends ApiLinkedResources<ConnectorRest,ConnectorBean,ConnexionRest,ConnexionBean> {

	@Autowired
	ApiConnexionResources apiConnexionResources;

	@Autowired
	ApiHrefConnectorResources apiHrefConnectorResources;

	/**
	 * constructor
	 */
	public ApiConnectorResources() {
		setRestClass(ConnectorRest.class);
		setBeanClass(ConnectorBean.class);
	}

	@Override
	public void mount() {
		/**
		 * connectors
		 */
		declare(CONNECTOR_RESOURCE);
		/**
		 * connectors -> connexions
		 */
		declare(CONNECTOR_RESOURCE, CONNEXION_RESOURCE, apiConnexionResources, apiHrefConnectorResources, CONNEXION, SORTKEY, HREF);
	}

	@Override
	public ResourcePair doRealTask(ConnectorBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case REGISTER:
				result = register(bean, args, new GenericMap());
				break;
			default:
				result = new GenericMap();
		}
		return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
	}

	private GenericMap register(ConnectorBean bean, GenericMap args, GenericMap properties) throws TechnicalNotFoundException {
		try {
			/**
			 * retrieve lin if exist
			 */
			ConnectorRest owner = apiHrefConnectorResources.toConnectorRest(bean);
			List<GenericEntity> links = apiHrefConnectorResources.findAll(owner, HREF.toString());
			if(links.size() == 0) {
				/**
				 * no links
				 */
				ConnexionRest r = new ConnexionRest();
				r.adress = (String) args.get("href");
				r.isRenderer = (boolean) args.get("isRenderer");
				r.isSensor = (boolean) args.get("isSensor");
				r.canAnswer = (boolean) args.get("canAnswer");
				/*
				 * build it
				 */
				apiConnexionResources.doCreate(r);
				apiHrefConnectorResources.add(
						owner, 
						apiConnexionResources.doCreate(r),
						new GenericMap(),
						CONNECTOR_RESOURCE.toString(),
						HREF.toString());
			}
		} catch (TechnicalNotFoundException e) {
			throw e;
		}
		return args;
	}
}
