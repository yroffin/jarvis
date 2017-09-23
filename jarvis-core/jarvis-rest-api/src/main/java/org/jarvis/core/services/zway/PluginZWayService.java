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

package org.jarvis.core.services.zway;

import javax.annotation.PostConstruct;

import org.common.core.exception.TechnicalException;
import org.jarvis.core.model.rest.plugin.ZwayRest;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.resources.api.plugins.ZwayJerseyClient;
import org.jarvis.core.services.PluginService;
import org.common.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
public class PluginZWayService extends PluginService {
	protected Logger logger = LoggerFactory.getLogger(PluginZWayService.class);

	@Autowired
	Environment env;

	@Autowired
	ApiPropertyResources apiPropertyResources;

	ZwayJerseyClient zwayJerseyClient;

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		/**
		 * initialize
		 */
		zwayJerseyClient = new ZwayJerseyClient(
				env.getProperty("jarvis.zway.url"),
				env.getProperty("jarvis.zway.user", (String) null),
				env.getProperty("jarvis.zway.password", (String) null),
				env.getProperty("jarvis.zway.timeout.connect","2000"),
				env.getProperty("jarvis.zway.timeout.read","2000"));
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public GenericMap asObject(GenericMap command, GenericMap args) throws TechnicalException {
    	String[] res = ((String) command.get("body")).split("_")[2].split("-");
    	ZwayRest zwayRest = zwayJerseyClient.call(res[0],res[1],res[2],res[3]);
    	return zwayRest.getExtended();
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public boolean asBoolean(GenericMap command, GenericMap args) throws TechnicalException {
		return false;
	}
}
