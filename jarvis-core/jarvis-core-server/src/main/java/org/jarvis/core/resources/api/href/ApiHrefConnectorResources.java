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

import javax.annotation.PostConstruct;

import org.jarvis.core.model.bean.connector.ConnectorBean;
import org.jarvis.core.model.bean.connector.ConnexionBean;
import org.jarvis.core.model.rest.connector.ConnectorRest;
import org.jarvis.core.model.rest.connector.ConnexionRest;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefConnectorResources extends ApiHrefMapper<ConnectorRest,ConnexionRest> {

	@PostConstruct
	protected
	void init() {
		super.init(ConnectorBean.class.getSimpleName(),ConnexionBean.class.getSimpleName(),"connexions");
	}

	/**
	 * convert to bean
	 * @param rest 
	 * @return ConnectorBean
	 */
	public ConnectorBean toConnectorBean(ConnectorRest rest) {
		return mapperFactory.getMapperFacade().map(rest, ConnectorBean.class);
	}

	/**
	 * convert to bean
	 * @param rest 
	 * @return ConnexionBean
	 */
	public ConnexionBean toConnexionBean(ConnexionRest rest) {
		return mapperFactory.getMapperFacade().map(rest, ConnexionBean.class);
	}

	/**
	 * convert to bean
	 * @param bean 
	 * @return ConnexionRest
	 */
	public ConnectorRest toConnectorRest(ConnectorBean bean) {
		return mapperFactory.getMapperFacade().map(bean, ConnectorRest.class);
	}

	/**
	 * convert to bean
	 * @param bean 
	 * @return ConnexionRest
	 */
	public ConnexionRest toConnexionRest(ConnexionBean bean) {
		return mapperFactory.getMapperFacade().map(bean, ConnexionRest.class);
	}
}
