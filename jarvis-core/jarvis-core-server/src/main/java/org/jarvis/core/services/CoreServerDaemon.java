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

package org.jarvis.core.services;

import org.jarvis.core.resources.CoreResources;
import org.jarvis.core.resources.api.connectors.ApiConnectorResources;
import org.jarvis.core.resources.api.connectors.ApiConnexionResources;
import org.jarvis.core.resources.api.iot.ApiEventResources;
import org.jarvis.core.resources.api.iot.ApiIotResources;
import org.jarvis.core.resources.api.iot.ApiTriggerResources;
import org.jarvis.core.resources.api.plugins.ApiCommandResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.resources.api.scenario.ApiBlockResources;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.jarvis.core.resources.api.tools.ApiToolResources;
import org.jarvis.core.resources.api.views.ApiViewResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
@PropertySource("classpath:server.properties")
public class CoreServerDaemon {

	@Autowired
	Environment env;

	@Autowired
	CoreResources coreResources;

	@Autowired
	ApiScenarioResources apiScenarioResources;

	@Autowired
	ApiBlockResources apiBlockResources;

	@Autowired
	ApiIotResources apiIotResources;

	@Autowired
	ApiViewResources apiViewResources;

	@Autowired
	ApiEventResources apiEventResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	ApiToolResources apiToolResources;

	@Autowired
	ApiConnectorResources apiConnectorResources;

	@Autowired
	ApiConnexionResources apiConnexionResources;

	/**
	 * start component
	 */
	public void server() {
		String iface = env.getProperty("jarvis.server.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.server.port"));
		spark.Spark.ipAddress(iface);

		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * mount resources
		 */
		coreResources.mount();
		/**
		 * mount plugin resources
		 */
		apiScenarioResources.mount();
		apiBlockResources.mount();
		apiIotResources.mount();
		apiViewResources.mount();
		/**
		 * mount plugin resources
		 */
		apiScriptPluginResources.mount();
		apiCommandResources.mount();
		apiEventResources.mount();
		apiTriggerResources.mount();
		/**
		 * tools
		 */
		apiToolResources.mount();
		apiConnectorResources.mount();
		apiConnexionResources.mount();
	}
}
