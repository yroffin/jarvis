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

import java.io.IOException;
import java.lang.reflect.Field;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.services.shell.PluginShellService;
import org.jarvis.core.type.CommandType;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Commande resources
 */
@Component
public class ApiCommandResources extends ApiResources<CommandRest,CommandBean> {

	/**
	 * constructor
	 */
	public ApiCommandResources() {
		setRestClass(CommandRest.class);
		setBeanClass(CommandBean.class);
	}

	@Autowired
	PluginShellService pluginShellService;

	@Autowired
	PluginGroovyService pluginGroovyService;

	/**
	 * execute task on command
	 * @param command
	 * @param args
	 * @param taskType
	 * @return String
	 * @throws Exception
	 */
	@Override
	public ResourcePair doRealTask(CommandBean command, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result = args;
		switch(taskType) {
			case EXECUTE:
				result = execute(command, result);
				break;
			default:
				result = new GenericMap();
		}
    	return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
	}

	/**
	 * execute this command
	 * @param command
	 * @param args
	 * @return GenericMap
	 */
	public GenericMap execute(CommandBean command, GenericMap args) {
		/**
		 * convert command to lazy map
		 */
		GenericMap converted = new GenericMap();
		for(Field field : command.getClass().getDeclaredFields()) {
			try {
				converted.put(field.getName(), field.get(command));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new TechnicalException(e);
			}
		}
		GenericMap result = null;
		try {
			switch(command.type) {
				case COMMAND:
					result = pluginShellService.command(converted, args);
					break;
				case SHELL:
					result = pluginShellService.shell(converted, args);
					break;
				case GROOVY:
					result = pluginGroovyService.groovy(converted, args);
					break;
				default:
			}
		} catch (IOException | InterruptedException e) {
			throw new TechnicalException(e);
		}
		try {
			logger.info("SCRIPT - OUTPUT  {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
    	return result;
	}
	
	@Override
	public void mount() {
		/**
		 * commands
		 */
		declare(COMMAND_RESOURCE);
	}
}
