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

import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jarvis.core.SwaggerParser;
import org.jarvis.core.model.bean.config.Oauth2Config;
import org.jarvis.core.resources.CoreMqttSystem;
import org.jarvis.core.resources.CoreResources;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.config.ApiConfigResources;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.resources.api.connectors.ApiConnectorResources;
import org.jarvis.core.resources.api.connectors.ApiDataSourceResources;
import org.jarvis.core.resources.api.device.ApiDeviceResources;
import org.jarvis.core.resources.api.device.ApiEventResources;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.plugins.ApiCommandResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.resources.api.plugins.ApiZwayPluginResources;
import org.jarvis.core.resources.api.scenario.ApiBlockResources;
import org.jarvis.core.resources.api.scenario.ApiScenarioResources;
import org.jarvis.core.resources.api.tools.ApiCronResources;
import org.jarvis.core.resources.api.tools.ApiNotificationResources;
import org.jarvis.core.resources.api.tools.ApiToolResources;
import org.jarvis.core.resources.api.views.ApiViewResources;
import org.jarvis.core.security.JarvisAccessLogFilter;
import org.jarvis.core.security.JarvisAuthorizerUsers;
import org.jarvis.core.security.JarvisCoreClient;
import org.jarvis.core.security.JarvisCoreProfile;
import org.jarvis.core.security.JarvisTokenValidationFilter;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.sparkjava.DefaultHttpActionAdapter;
import org.pac4j.sparkjava.SparkWebContext;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

/**
 * main daemon
 */
@Component
@PropertySources({ @PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file://${jarvis.user.dir}/config.properties", ignoreResourceNotFound = true) })
@SwaggerDefinition(info = @Info(description = "Jarvis", version = "v1.0", //
		title = "Jarvis core system", contact = @Contact(name = "Yannick Roffin", url = "https://yroffin.github.io")), schemes = {
				SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS }, consumes = {
						"application/json" }, produces = { "application/json" }, tags = { @Tag(name = "swagger") })
public class CoreServerDaemon {
	protected Logger logger = LoggerFactory.getLogger(CoreServerDaemon.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	Environment env;

	@Autowired
	CoreEventDaemon coreEventDaemon;

	@Autowired
	CoreSphinxService coreSphinxService;

	@Autowired
	CoreResources coreResources;

	@Autowired
	CoreMqttSystem coreMqttSystem;

	@Autowired
	ApiScenarioResources apiScenarioResources;

	@Autowired
	ApiDataSourceResources apiDataSourceResources;

	@Autowired
	ApiZwayPluginResources apiZwayPluginResources;

	@Autowired
	ApiBlockResources apiBlockResources;

	@Autowired
	ApiDeviceResources apiDeviceResources;

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
	ApiNotificationResources apiNotificationResources;

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
		/**
		 * init mapper
		 */
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new JodaModule());

		for (String key : ImmutableList.of("jarvis.user.dir", "jarvis.log.dir", "jarvis.server.url", "jarvis.neo4j.url",
				"jarvis.rflink.comport", "jarvis.sunset.sunrise.url")) {
			logger.info("{} = {}", key, env.getProperty(key));
		}

		String iface = env.getProperty("jarvis.server.interface");
		int port = Integer.parseInt(env.getProperty("jarvis.server.port"));
		spark.Spark.ipAddress(iface);
		spark.Spark.threadPool(Integer.parseInt(env.getProperty("jarvis.server.pool.thread", "32")));

		/**
		 * port
		 */
		spark.Spark.port(port);

		/**
		 * mount resources
		 */
		if (System.getProperty("profile") != null && System.getProperty("profile").equals("dev")) {
			coreResources.mountLocal();
			spark.Spark.staticFiles.expireTime(1);
		} else {
			coreResources.mountExternal();
			spark.Spark.staticFiles.expireTime(1);
		}

		/**
		 * build security config
		 */
		final Clients clients = new Clients(new JarvisCoreClient());
		final Config config = new Config(clients);
		final String[] users = env.getProperty("jarvis.oauth2.users", "empty").split(",");
		config.addAuthorizer("usersCheck", new JarvisAuthorizerUsers(users));
		config.setHttpActionAdapter(new DefaultHttpActionAdapter());

		/**
		 * all api must be validated with token
		 */
		final String[] excludes = env.getProperty("jarvis.oauth2.excludes", "").split(",");
		spark.Spark.before("/api/*", new JarvisTokenValidationFilter(config, "JarvisCoreClient",
				"securityHeaders,csrfToken,usersCheck", excludes));

		/**
		 * ident api
		 */
		spark.Spark.get("/api/profile/me", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				/**
				 * in exclude mode return a fake profile
				 */
				for (String exclude : excludes) {
					if (request.ip().matches(exclude)) {
						return "{\"attributes\":{\"email\":\"-\"}}";
					}
				}

				response.type("application/json");

				/**
				 * retrieve profile from context
				 */
				WebContext context = new SparkWebContext(request, response);
				ProfileManager<JarvisCoreProfile> manager = new ProfileManager<JarvisCoreProfile>(context);
				Optional<JarvisCoreProfile> profile = manager.get(true);

				return mapper.writeValueAsString(profile.get());
			}
		});

		/**
		 * retrieve if credential are needed
		 */
		spark.Spark.get("/api/connect", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				/**
				 * no protection on excluded ips
				 */
				for (String exclude : excludes) {
					if (request.ip().matches(exclude)) {
						return false;
					}
				}
				return true;
			}
		});

		/**
		 * retrieve oauth2 client and identity
		 */
		spark.Spark.get("/api/oauth2", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				Oauth2Config oauth2Config = new Oauth2Config();
				if (request.queryParamsValues("client")[0].equals("google")) {
					oauth2Config.type = "google";
					oauth2Config.redirect = request.queryParamsValues("oauth2_redirect_uri")[0];
					oauth2Config.key = env.getProperty("jarvis.oauth2.google");
					oauth2Config.url = "https://accounts.google.com/o/oauth2/auth?scope=email&client_id="
							+ oauth2Config.key + "&response_type=token&redirect_uri=" + oauth2Config.redirect;
				}
				if (request.queryParamsValues("client")[0].equals("facebook")) {
					oauth2Config.type = "facebook";
					oauth2Config.redirect = request.queryParamsValues("oauth2_redirect_uri")[0];
					oauth2Config.key = env.getProperty("jarvis.oauth2.facebook");
					oauth2Config.url = "https://www.facebook.com/dialog/oauth?scope=email&client_id=" + oauth2Config.key
							+ "&response_type=token&redirect_uri=" + oauth2Config.redirect;
				}
				return mapper.writeValueAsString(oauth2Config);
			}
		});

		/**
		 * mount all standard resources
		 */
		mountAllResources("org.jarvis.core.resources.api");

		/**
		 * Build swagger json description
		 */
		final String swaggerJson;
		swaggerJson = SwaggerParser.getSwaggerJson("org.jarvis.core");
		spark.Spark.get("/api/swagger", (req, res) -> {
			return swaggerJson;
		});

		/**
		 * swagger ui resources
		 */
		spark.Spark.get("/swagger-ui/*", (req, res) -> {
			String resource = "public/swagger-ui" + req.uri().replace("/swagger-ui", "");
			logger.info("uri: {}", resource);
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
			if (is != null) {
				return IOUtils.toByteArray(is);
			} else {
				res.status(404);
				return "";
			}
		});

		Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
		Parser parser = Parser.builder().extensions(EXTENSIONS).build();
		HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();

		/**
		 * swagger ui resources
		 */
		spark.Spark.get("/api/helps/*", (req, res) -> {
			String resource = "public/helps" + req.uri().replace("/api/helps", "");
			logger.info("uri: {}", resource);
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(resource);
			if (is != null) {
				if(resource.endsWith("markdown")) {
					res.type("text/html;charset=UTF-8");
				}
				if(resource.endsWith("png")) {
					res.type("image/x-png");
				}
				Node document = parser.parse(IOUtils.toString(is));
				return renderer.render(document);
			} else {
				res.status(404);
				return "";
			}
		});

		spark.Spark.after("/*", new JarvisAccessLogFilter());

		/**
		 * init trigger subscription
		 */
		coreEventDaemon.triggers();
	}

	/**
	 * mount all resources in package
	 * 
	 * @param packageName
	 */
	protected void mountAllResources(String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> apiClasses = reflections.getTypesAnnotatedWith(Api.class);
		for (Class<?> klass : apiClasses) {
			ApiResources<?, ?> bean = (ApiResources<?, ?>) applicationContext.getBean(klass);
			logger.debug("Mount resource {}", bean);
			bean.mount();
		}
	}

	protected final Logger accesslog = LoggerFactory.getLogger(JarvisAccessLogFilter.class);
}
