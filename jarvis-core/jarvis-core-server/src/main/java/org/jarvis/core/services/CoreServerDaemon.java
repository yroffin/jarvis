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


import javax.annotation.PostConstruct;

import org.jarvis.core.model.bean.config.Oauth2Config;
import org.jarvis.core.resources.CoreResources;
import org.jarvis.core.resources.CoreWebsocket;
import org.jarvis.core.resources.api.config.ApiConfigResources;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.resources.api.connectors.ApiConnectorResources;
import org.jarvis.core.resources.api.iot.ApiEventResources;
import org.jarvis.core.resources.api.iot.ApiIotResources;
import org.jarvis.core.resources.api.iot.ApiTriggerResources;
import org.jarvis.core.resources.api.plugins.ApiCommandResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.resources.api.scenario.ApiBlockResources;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.jarvis.core.resources.api.tools.ApiCronResources;
import org.jarvis.core.resources.api.tools.ApiToolResources;
import org.jarvis.core.resources.api.views.ApiViewResources;
import org.jarvis.core.security.JarvisCoreClient;
import org.jarvis.core.security.JarvisTokenValidationFilter;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * main daemon
 */
@Component
@PropertySources({
	@PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true)
})
public class CoreServerDaemon {
	protected Logger logger = LoggerFactory.getLogger(CoreServerDaemon.class);

	@Autowired
	Environment env;

	@Autowired
	CoreResources coreResources;
	
	@Autowired
	CoreWebsocket coreWebsocket;

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
	ApiConfigResources apiConfigResources;

	@Autowired
	ApiScriptPluginResources apiScriptPluginResources;

	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiTriggerResources apiTriggerResources;

	@Autowired
	ApiToolResources apiToolResources;

	@Autowired
	ApiCronResources apiCronResources;

	@Autowired
	ApiConnectorResources apiConnectorResources;

	@Autowired
	ApiPropertyResources apiPropertyResources;

	protected ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * start component
	 */
	@PostConstruct
	public void server() {
		
		for(String key : ImmutableList.of(
				"jarvis.user.dir",
				"jarvis.log.dir",
				"jarvis.server.url",
				"jarvis.neo4j.url",
				"jarvis.elasticsearch.url",
				"jarvis.sunset.sunrise.url")) {
			logger.info("{} = {}", key, env.getProperty(key));
		}
		
		String iface = env.getProperty("jarvis.server.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.server.port"));
		spark.Spark.ipAddress(iface);
		spark.Spark.threadPool(Integer.parseInt(env.getProperty("jarvis.server.pool.thread","8")));
		
		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * websockets
		 */
		coreWebsocket.mount();

		/**
		 * mount resources
		 */
		if(System.getProperty("profile") != null && System.getProperty("profile").equals("dev")) {
			coreResources.mountLocal();
		} else {
			coreResources.mountExternal();
		}
		
		/**
		 * build security config
		 */
		final Clients clients = new Clients(new JarvisCoreClient());
		final Config config = new Config(clients);
		config.setHttpActionAdapter(new DefaultHttpActionAdapter());
		
		/**
		 * all api must be validated with token
		 */
		final String[] excludes = env.getProperty("jarvis.oauth2.excludes","").split(",");
		spark.Spark.before("/api/*", new JarvisTokenValidationFilter(config, "JarvisCoreClient", "securityHeaders,csrfToken", excludes));
		
		/**
		 * ident api
		 */
		spark.Spark.get("/api/profile/me", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	/**
		    	 * in exclude mode return a fake profile
		    	 */
		        for(String exclude : excludes) {
		        	if(request.ip().matches(exclude)) {
		                return "{\"attributes\":{\"email\":\"-\"}}";
		        	}
		        }

		        UserProfile userProfile = request.session().attribute("pac4jUserProfile");
				return mapper.writeValueAsString(userProfile);
		    }
		});

		/**
		 * retrieve oauth2 client and identity
		 */
		spark.Spark.get("/oauth2", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
		    	Oauth2Config oauth2Config = new Oauth2Config();
		    	if(request.queryParamsValues("client")[0].equals("google")) {
			    	oauth2Config.type = "google";
			    	oauth2Config.redirect = request.queryParamsValues("oauth2_redirect_uri")[0];
			    	oauth2Config.key = env.getProperty("jarvis.oauth2.google");
			    	oauth2Config.url = "https://accounts.google.com/o/oauth2/auth?scope=email&client_id="+oauth2Config.key+"&response_type=token&redirect_uri="+oauth2Config.redirect;
		    	}
		    	if(request.queryParamsValues("client")[0].equals("facebook")) {
			    	oauth2Config.type = "facebook";
			    	oauth2Config.redirect = request.queryParamsValues("oauth2_redirect_uri")[0];
			    	oauth2Config.key = env.getProperty("jarvis.oauth2.facebook");
			    	oauth2Config.url = "https://www.facebook.com/dialog/oauth?scope=email&client_id="+oauth2Config.key+"&response_type=token&redirect_uri="+oauth2Config.redirect;
		    	}
				return mapper.writeValueAsString(oauth2Config);
		    }
		});
		
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
		apiCronResources.mount();
		apiToolResources.mount();
		apiConnectorResources.mount();
		apiConfigResources.mount();
		apiPropertyResources.mount();

		spark.Spark.after((request, response) -> {
		    response.header("Content-Encoding", "gzip");
		});
	}
}
