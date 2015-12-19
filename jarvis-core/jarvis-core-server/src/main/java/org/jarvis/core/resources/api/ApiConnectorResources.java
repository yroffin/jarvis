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

package org.jarvis.core.resources.api;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.ConnectorBean;
import org.jarvis.core.model.rest.ConnectorRest;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

@Component
public class ApiConnectorResources extends ApiResources<ConnectorRest,ConnectorBean> {

	@Override
	public List<ConnectorRest> doFindAll() {
		List<ConnectorRest> result = new ArrayList<ConnectorRest>();
		for(ConnectorBean item : apiService.findAll()) {
			result.add(mapperFactory.getMapperFacade().map(item, ConnectorRest.class));
		}
		return result;
	}

	@Override
	public ConnectorRest doGetById(String id) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(apiService.getById(id), ConnectorRest.class);
	}

	@Override
	public ConnectorRest doCreate(ConnectorRest rest) {
		return mapperFactory.getMapperFacade().map(
				apiService.create(mapperFactory.getMapperFacade().map(rest, ConnectorBean.class)),
				ConnectorRest.class);
	}

	@Override
	public ConnectorRest doUpdate(String id, ConnectorRest rest) throws TechnicalNotFoundException {
		return mapperFactory.getMapperFacade().map(
				apiService.update(id, mapperFactory.getMapperFacade().map(rest, ConnectorBean.class)), 
				ConnectorRest.class);
	}

	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/connectors", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/connectors/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/connectors", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ConnectorRest.class);
		    }
		});
		put("/api/connectors/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, ConnectorRest.class);
		    }
		});
	}
}
