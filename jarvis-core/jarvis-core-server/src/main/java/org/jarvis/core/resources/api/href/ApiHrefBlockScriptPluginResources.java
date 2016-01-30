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

import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.bean.scenario.BlockBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.model.rest.scenario.BlockRest;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefBlockScriptPluginResources extends ApiHrefMapper<BlockRest,ScriptPluginRest> {

	@PostConstruct
	protected
	void init() {
		super.init(BlockBean.class.getSimpleName(),ScriptPluginBean.class.getSimpleName(),"plugins");
	}

	/**
	 * find all (filtered by HREF_IF)
	 * @param bean 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAllConditions(BlockBean bean) {
		return super.findAll(mapperFactory.getMapperFacade().map(bean, BlockRest.class), HREF_IF);
	}

	/**
	 * find all (filtered by HREF_IF)
	 * @param bean 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAllThen(BlockBean bean) {
		return super.findAll(mapperFactory.getMapperFacade().map(bean, BlockRest.class), HREF_THEN);
	}

	/**
	 * find all (filtered by HREF_IF)
	 * @param bean 
	 * @return List<GenericEntity>
	 */
	public List<GenericEntity> findAllElse(BlockBean bean) {
		return super.findAll(mapperFactory.getMapperFacade().map(bean, BlockRest.class), HREF_THEN);
	}
}
