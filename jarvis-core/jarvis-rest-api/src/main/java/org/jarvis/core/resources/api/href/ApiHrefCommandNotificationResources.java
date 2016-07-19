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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.tools.NotificationBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.tools.NotificationRest;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.jarvis.core.resources.api.tools.ApiNotificationResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefCommandNotificationResources extends ApiHrefMapper<CommandRest,NotificationRest> {

	@Autowired
	ApiNotificationResources apiNotificationResources;
	
	@PostConstruct
	protected
	void init() {
		super.init(CommandBean.class.getSimpleName(),NotificationBean.class.getSimpleName(),"notifications");
	}

	/**
	 * find all by bean
	 * @param script
	 * @return List<NotificationRest>
	 */
	public List<NotificationBean> findAll(CommandBean script) {
		List<NotificationBean> result = new ArrayList<NotificationBean>();
		for( GenericEntity entity : super.findAll(mapperFactory.getMapperFacade().map(script, CommandRest.class), HREF) ) {
			try {
				result.add(apiNotificationResources.doGetByIdBean(entity.id));
			} catch (TechnicalNotFoundException e) {
				throw new TechnicalException(e);
			}
		}
		return result;
	}
}
