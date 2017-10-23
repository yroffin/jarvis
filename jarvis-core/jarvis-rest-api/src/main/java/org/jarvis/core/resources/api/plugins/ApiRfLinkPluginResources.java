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

package org.jarvis.core.resources.api.plugins;

import static spark.Spark.put;

import javax.annotation.PostConstruct;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.jarvis.core.model.bean.plugin.RfLinkBean;
import org.jarvis.core.model.rest.plugin.RfLinkRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.services.CoreRflink;
import org.common.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * rflink resource
 */
@Api(value = "rflink")
@Path("/api/rflink/chacon")
@Produces("application/json")
@Component
public class ApiRfLinkPluginResources extends ApiResources<RfLinkRest,RfLinkBean> {
	protected Logger logger = LoggerFactory.getLogger(ApiRfLinkPluginResources.class);
	
	@Autowired
	Environment env;

	@Autowired
	CoreRflink coreRflink;

	@PostConstruct
	public void init() {
		/**
		 * constructor
		 */
		setRestClass(RfLinkRest.class);
		setBeanClass(RfLinkBean.class);
	}
	
	@Override
	public void mount() {
		/**
		 * devices
		 */
		put("/api/rflink/chacon/:id/:sw", chacon());
	}

	/**
	 * @return Route
	 */
	@PUT
	@Path("/{id}/{sw}")
	@ApiOperation(value = "Send chacon command thru rflink driver", nickname="chacon")
	@ApiImplicitParams({
			@ApiImplicitParam(required = true, dataType="String", name="id", paramType = "path"),
			@ApiImplicitParam(required = true, dataType="String", name="sw", paramType = "path"),
			@ApiImplicitParam(required = true, dataType="String", name="command", paramType = "body"),
	})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success", response=String.class)
	})
	public Route chacon() {
		return  new Route() {
		    @Override
			public Object handle(Request request, Response response) {
		    	String id = request.params(":id");
		    	String sw = request.params(":sw");
		    	return coreRflink.chacon(id, sw, request.body());
		    }
		};
	}

	/**
	 * default task
	 */
	@Override
	public GenericValue doRealTask(RfLinkBean bean, GenericMap args, String taskType) throws TechnicalException {
		return new GenericValue(new GenericMap());
	}
}
