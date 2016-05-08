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

import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalHttpException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

/**
 * View resource
 */
@Component
public class ApiConnectorResources extends ApiResources<ConnectorRest,ConnectorBean> {

	protected Client client;

	/**
	 * constructor
	 */
	public ApiConnectorResources() {
		setRestClass(ConnectorRest.class);
		setBeanClass(ConnectorBean.class);

		// create HTTP Client for all call
		this.client = ClientBuilder.newClient();
	}

	@Override
	public void mount() {
		/**
		 * connectors
		 */
		declare(CONNECTOR_RESOURCE);
	}

	@Override
	public GenericValue doRealTask(ConnectorBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case PING:
				result = ping(bean, args, new GenericMap());
				break;
			default:
				result = new GenericMap();
		}
		return new GenericValue(mapper.writeValueAsString(result));
	}

	private GenericMap ping(ConnectorBean bean, GenericMap args, GenericMap properties) throws TechnicalNotFoundException, TechnicalHttpException {
		/**
		 * build call
		 */
		Response entity = client.target(bean.adress)
	            .request(MediaType.APPLICATION_JSON)
	            .accept(MediaType.APPLICATION_JSON)
	            .acceptEncoding("charset=UTF-8")
	            .get();

		if(entity.getStatus() == 200) {
			GenericMap body = null;
			try {
				body = mapper.readValue(entity.readEntity(String.class), GenericMap.class);
			} catch (IOException e) {
				throw new TechnicalException(e);
			}
			return body;
		} else {
			throw new TechnicalHttpException(entity.getStatus(), bean.adress);
		}
	}
}
