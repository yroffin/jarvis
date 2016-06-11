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

import java.lang.reflect.Field;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.services.CoreStatistics;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.services.shell.PluginShellService;
import org.jarvis.core.type.GenericMap;
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
	public GenericValue doRealTask(CommandBean command, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result = args;
		switch(taskType) {
			case EXECUTE:
				return new GenericValue(mapper.writeValueAsString(execute(command, result)));
			case TEST:
				return new GenericValue(mapper.writeValueAsString(test(command, result)));
			default:
				result = new GenericMap();
		}
    	return new GenericValue(mapper.writeValueAsString(result));
	}

	/**
	 * transform CommandBean to GenericMap
	 * @param command
	 * @return
	 */
	private GenericMap extractCommand(CommandBean command) {
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
		return converted;
	}

	/**
	 * execute this command
	 * @param command
	 * @param args
	 * @return boolean
	 */
	public boolean test(CommandBean command, GenericMap args) {
		boolean result = false;
		try {
			logger.info("COMMAND TEST - INPUT {}\n{}", command.type, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(args));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		try {
			switch(command.type) {
				case COMMAND:
					result = pluginShellService.asBoolean(extractCommand(command), args);
					break;
				case SHELL:
					result = pluginShellService.asBoolean(extractCommand(command), args);
					break;
				case GROOVY:
					result = pluginGroovyService.asBoolean(extractCommand(command), args);
					break;
				default:
			}
		} catch (TechnicalException e) {
			throw new TechnicalException(e);
		}
		try {
			logger.info("COMMAND TEST - OUTPUT\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
    	return result;
	}

	@Autowired
	CoreStatistics coreStatistics;

	/**
	 * execute this command
	 * @param command
	 * @param args
	 * @return GenericMap
	 */
	public GenericMap execute(CommandBean command, GenericMap args) {
		GenericMap result = null;
		try {
			logger.info("COMMAND - INPUT {}\n{}", command.type, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(args));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
		try {
			switch(command.type) {
				case COMMAND:
					result = pluginShellService.asObject(extractCommand(command), args);
					break;
				case SHELL:
					result = pluginShellService.asObject(extractCommand(command), args);
					break;
				case GROOVY:
					result = pluginGroovyService.asObject(extractCommand(command), args);
					break;
				default:
			}
		} catch (TechnicalException e) {
			throw new TechnicalException(e);
		}
		try {
			/**
			 * store command execution in statistics
			 */
			coreStatistics.write(command, args, result);
			logger.info("COMMAND - OUTPUT\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
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
