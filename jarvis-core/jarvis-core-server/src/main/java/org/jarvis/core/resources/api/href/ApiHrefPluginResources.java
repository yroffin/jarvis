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

import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.PluginBean;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.plugin.PluginRest;
import org.jarvis.core.resources.api.mapper.ApiHrefMapper;
import org.springframework.stereotype.Component;

/**
 * HREF handler
 */
@Component
public class ApiHrefPluginResources extends ApiHrefMapper<PluginRest,CommandRest> {

	@PostConstruct
	protected
	void init() {
		super.init(PluginBean.class.getSimpleName(),CommandBean.class.getSimpleName());
	}
}
