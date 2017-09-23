/**
 *  Copyright 2017 Yannick Roffin
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

package org.jarvis.core.services.rflink;

import javax.annotation.PostConstruct;

import org.common.core.exception.TechnicalException;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.services.CoreRflink;
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
public class PluginRfLinkService extends PluginService {
	protected Logger logger = LoggerFactory.getLogger(PluginRfLinkService.class);

	@Autowired
	Environment env;

	@Autowired
	ApiPropertyResources apiPropertyResources;

	@Autowired
	CoreRflink coreRflink;

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public GenericMap asObject(GenericMap command, GenericMap args) throws TechnicalException {
		/**
		 * sample
		 * CHACON 00000 1 ON|OFF
		 */
    	String[] res = parse((String) command.get("body"), args).split(" ");
    	String value = "";
    	if(res.length == 4 && res[0].equals("CHACON") && !res[1].contains("$") && !res[2].contains("$") && !res[3].contains("$")) {
    		value = coreRflink.chacon(res[1], res[2], res[3]);
    	}
    	GenericMap ret = new GenericMap();
    	ret.put("result", value);
    	return ret;
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
