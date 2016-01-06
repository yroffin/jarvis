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

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.jarvis.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.href.ApiHrefPluginCommandResources;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * script plugin resource
 */
@Component
public class ApiScriptPluginResources extends ApiResources<ScriptPluginRest,ScriptPluginBean> {

	@Autowired
	ApiCommandResources apiCommandResources;

	@Autowired
	ApiHrefPluginCommandResources apiHrefPluginCommandResources;

	/**
	 * constructor
	 */
	public ApiScriptPluginResources() {
		setRestClass(ScriptPluginRest.class);
		setBeanClass(ScriptPluginBean.class);
	}
	
	@Override
	public void mount() {
		/**
		 * mount resources
		 */
		get("/api/plugins/scripts", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doFindAll(request, response);
		    }
		});
		get("/api/plugins/scripts/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doGetById(request, ":id", response);
		    }
		});
		post("/api/plugins/scripts", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doCreate(request, response, ScriptPluginRest.class);
		    }
		});
		post("/api/plugins/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doTask(request, ":id", "task", response, ScriptPluginRest.class);
		    }
		});
		put("/api/plugins/scripts/:id", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	return doUpdate(request, ":id", response, ScriptPluginRest.class);
		    }
		});
		/**
		 * commands
		 */
		get("/api/plugins/scripts/:id/commands", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest master = doGetById(request.params(ID));
		    		List<CommandRest> result = new ArrayList<CommandRest>();
		    		for(GenericEntity link : apiHrefPluginCommandResources.findAll(master)) {
		    			result.add(commandRest(link));
		    		}
		    		
		    		/**
		    		 * sort by order
		    		 */
		    		Collections.sort(result, new Comparator<CommandRest>() {

						@Override
						public int compare(CommandRest l, CommandRest r) {
							String left = (String) l.get("order");
							if(left == null) {
								return -1;
							}
							String right = (String) r.get("order");
							return left.compareTo(right);
						}
		    			
		    		});
			    	return mapper.writeValueAsString(result);
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		/**
		 * create new link
		 */
		post("/api/plugins/scripts/:id/commands/:command", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest script = doGetById(request.params(ID));
			    	try {
			    		CommandRest command = apiCommandResources.doGetById(request.params(COMMAND));
				    	GenericEntity instance = apiHrefPluginCommandResources.add(script, command, new GenericMap(request.body()), "commands");
				    	return mapper.writeValueAsString(commandRest(instance));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		/**
		 * update link
		 */
		put("/api/plugins/scripts/:id/commands/:command/:instance", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		doGetById(request.params(":id"));
			    	try {
			    		apiCommandResources.doGetById(request.params(COMMAND));
			    		GenericMap properties = apiHrefPluginCommandResources.update(request.params(INSTANCE), new GenericMap(request.body()));
				    	return mapper.writeValueAsString(properties);
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    }
		});
		/**
		 * delete one link
		 */
		delete("/api/plugins/scripts/:id/commands/:command", new Route() {
		    @Override
			public Object handle(Request request, Response response) throws Exception {
		    	try {
		    		ScriptPluginRest plugin = doGetById(request.params(":id"));
			    	try {
			    		CommandRest command = apiCommandResources.doGetById(request.params(":command"));
			    		apiHrefPluginCommandResources.remove(plugin, command, request.queryParams("instance"));
			    	} catch(TechnicalNotFoundException e) {
			    		response.status(404);
			    		return "";
			    	}
		    	} catch(TechnicalNotFoundException e) {
		    		response.status(404);
		    		return "";
		    	}
		    	return doGetById(request, ":id", response);
		    }
		});
	}

	/**
	 * build command with relationship
	 * @param link
	 * @return CommandRest
	 * @throws TechnicalNotFoundException 
	 */
	public CommandRest commandRest(GenericEntity link) throws TechnicalNotFoundException {
		CommandRest commandRest = apiCommandResources.doGetById(link.id);
		commandRest.instance = link.instance;
		for(Entry<String, Object> property : link.get()) {
			commandRest.put(property.getKey(), property.getValue());
		}
		return commandRest;
	}

	@Override
	public String doRealTask(ScriptPluginBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case EXECUTE:
				result = execute(bean, args, new GenericMap());
				break;
			default:
				result = new GenericMap();
		}
		return mapper.writeValueAsString(result);
	}

	/**
	 * execute all command of this script as a pipeline
	 * @param script
	 * @param args 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public GenericMap execute(ScriptPluginRest script, GenericMap args) throws TechnicalNotFoundException {
		return execute(mapperFactory.getMapperFacade().map(script, ScriptPluginBean.class), args, new GenericMap());
	}

	/**
	 * execute all command of this script as a pipeline
	 * @param script
	 * @param args 
	 * @param output 
	 * @return GenericMap
	 * @throws TechnicalNotFoundException 
	 */
	public GenericMap execute(ScriptPluginBean script, GenericMap args, GenericMap output) throws TechnicalNotFoundException {
		GenericMap result = args;
		int index = 0;
		for(GenericEntity entity : apiHrefPluginCommandResources.findAll(script)) {
			CommandRest command = apiCommandResources.doGetById(entity.id);
			result = apiCommandResources.execute(mapperFactory.getMapperFacade().map(command, CommandBean.class), result);
			if(entity.get("name") != null) {
				output.put((String) entity.get("name"), result);
			} else {
				output.put("field"+(index++), result);
			}
		}
		return output;
	}
}
