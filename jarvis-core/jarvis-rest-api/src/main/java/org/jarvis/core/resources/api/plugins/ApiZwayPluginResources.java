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

package org.jarvis.core.resources.api.plugins;

import static spark.Spark.get;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.plugin.ZwayBean;
import org.jarvis.core.model.rest.plugin.ZwayRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * zway resource
 */
@Api(value = "zway")
@Component
public class ApiZwayPluginResources extends ApiResources<ZwayRest,ZwayBean> {
	protected Logger logger = LoggerFactory.getLogger(ApiZwayPluginResources.class);
	
	@Autowired
	Environment env;

	ZwayJerseyClient zwayJerseyClient;

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
		/**
		 * constructor
		 */
		setRestClass(ZwayRest.class);
		setBeanClass(ZwayBean.class);
	}
	
	@Override
	public void mount() {
		/**
		 * devices
		 */
		get("/api/zway/element/:element", new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	String[] args = request.params(":element").split("_")[2].split("-");
		    	ZwayRest zwayRest = zwayJerseyClient.call(args[0],args[1],args[2],args[3]);
		    	try {
		    		response.type("application/json");
					return mapper.writeValueAsString(zwayRest.getExtended());
				} catch (JsonProcessingException e) {
					logger.warn("While transform to json : {}", e);
				}
				return zwayRest;
		    }
		});
	}

	/**
	 * default task
	 */
	@Override
	public GenericValue doRealTask(ZwayBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		return new GenericValue(new GenericMap());
	}
}
