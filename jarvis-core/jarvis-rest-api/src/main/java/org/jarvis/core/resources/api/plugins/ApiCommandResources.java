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
import org.jarvis.core.model.bean.tools.NotificationBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.tools.NotificationRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefCommandNotificationResources;
import org.jarvis.core.resources.api.tools.ApiNotificationResources;
import org.jarvis.core.services.CoreStatistics;
import org.jarvis.core.services.groovy.PluginGroovyService;
import org.jarvis.core.services.shell.PluginShellService;
import org.jarvis.core.services.zway.PluginZWayService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Commande resources
 */
@Component
public class ApiCommandResources extends ApiLinkedResources<CommandRest,CommandBean,NotificationRest,NotificationBean> {

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
	ApiNotificationResources apiNotificationResources;

	@Autowired
	ApiHrefCommandNotificationResources apiHrefCommandNotificationResources;

	@Autowired
	PluginGroovyService pluginGroovyService;

	@Autowired
	PluginZWayService pluginZWayService;
	
	@Override
	public void mount() {
		/**
		 * commands
		 */
		declare(COMMAND_RESOURCE);
		/**
		 * scripts -> commands
		 */
		declare(COMMAND_RESOURCE, NOTIFICATION_RESOURCE, apiNotificationResources, apiHrefCommandNotificationResources, NOTIFICATION, SORTKEY, HREF);
	}

	/**
	 * execute task on command
	 * @param command
	 * @param args
	 * @param taskType
	 * @return String
	 */
	@Override
	public GenericValue doRealTask(CommandBean command, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result = args;
		switch(taskType) {
			case EXECUTE:
				try {
					return new GenericValue(mapper.writeValueAsString(execute(command, result)));
				} catch (JsonProcessingException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			case TEST:
				try {
					return new GenericValue(mapper.writeValueAsString(test(command, result)));
				} catch (JsonProcessingException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
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
				case ZWAY:
					result = pluginZWayService.asBoolean(extractCommand(command), args);
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
				case ZWAY:
					result = pluginZWayService.asObject(extractCommand(command), args);
					break;
				default:
			}
		} catch (TechnicalException e) {
			throw new TechnicalException(e);
		}
		/**
		 * iterate on each notification
		 */
		GenericMap payload = new GenericMap();
		payload.put("text", "Notification de commande");
		for(NotificationBean notification : apiHrefCommandNotificationResources.findAll(command)) {
			payload.put("title", command.name);
			payload.put("subtext", args + "");
			apiNotificationResources.doNotification(notification, payload);
		}
		/**
		 * store command execution in statistics
		 */
		try {
			coreStatistics.write(command, args, result);
			logger.info("COMMAND - OUTPUT\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			throw new TechnicalException(e);
		}
    	return result;
	}
}
