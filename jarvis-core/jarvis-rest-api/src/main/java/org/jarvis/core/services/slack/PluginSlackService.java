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

package org.jarvis.core.services.slack;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.resources.api.plugins.PayloadBean;
import org.jarvis.core.resources.api.plugins.SlackJerseyClient;
import org.jarvis.core.services.PluginService;
import org.jarvis.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
public class PluginSlackService extends PluginService {
	protected Logger logger = LoggerFactory.getLogger(PluginSlackService.class);

	@Autowired
	Environment env;

	@Autowired
	ApiPropertyResources apiPropertyResources;
	
	SlackJerseyClient slackJerseyClient;

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		slackJerseyClient = new SlackJerseyClient(
				env.getProperty("jarvis.slack.url","https://hooks.slack.com/services"),
				env.getProperty("jarvis.slack.user", (String) null),
				env.getProperty("jarvis.slack.password", (String) null),
				env.getProperty("jarvis.slack.timeout.connect","2000"),
				env.getProperty("jarvis.slack.timeout.read","2000"));
	}

	/**
	 * @param notification
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public GenericMap asObject(GenericMap notification, PayloadBean args) throws TechnicalException {
		slackJerseyClient.call(env.getProperty("jarvis.slack.api",""), args);
		return notification;
	}

	/**
	 * @param notification
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public boolean asBoolean(GenericMap notification, GenericMap args) throws TechnicalException {
		return false;
	}
}
