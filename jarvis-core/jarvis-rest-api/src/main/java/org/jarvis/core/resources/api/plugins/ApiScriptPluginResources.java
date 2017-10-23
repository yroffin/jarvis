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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Map.Entry;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.device.DeviceBean;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.CommandRest;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ApiLinkedResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.DeclareHrefResource;
import org.jarvis.core.resources.api.DeclareLinkedResource;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.href.ApiHrefPluginCommandResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.type.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

/**
 * script plugin resource
 */
@Api(value = "plugin")
@Path("/api/plugins")
@Produces("application/json")
@Component
@Declare(resource = ApiMapper.SCRIPT_RESOURCE, summary = "Plugin resource", rest = ScriptPluginRest.class)
public class ApiScriptPluginResources
		extends ApiLinkedResources<ScriptPluginRest, ScriptPluginBean, CommandRest, CommandBean> {
	protected Logger logger = LoggerFactory.getLogger(ApiScriptPluginResources.class);

	/**
	 * commands link
	 */
	@Autowired
	@DeclareLinkedResource(role = ApiMapper.COMMAND_RESOURCE, param = ApiMapper.COMMAND, sortKey = ApiMapper.SORTKEY)
	public ApiCommandResources apiCommandResources;

	/**
	 * commands link
	 */
	@Autowired
	@DeclareHrefResource(role = ApiMapper.COMMAND_RESOURCE, href = ApiMapper.HREF, target = CommandRest.class)
	public ApiHrefPluginCommandResources apiHrefPluginCommandResources;

	@Autowired
	Environment env;

	/**
	 * constructor
	 */
	public ApiScriptPluginResources() {
		setRestClass(ScriptPluginRest.class);
		setBeanClass(ScriptPluginBean.class);
	}

	@Override
	public void mount() {
		super.mount();
		GraphvizUtils.setDotExecutable(
				StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env.getProperty("jarvis.dot.executable")));
	}

	/**
	 * all script have two phase : a render (data) phase and an execute (action)
	 * phase
	 */
	@Override
	public GenericValue doRealTask(ScriptPluginBean bean, GenericMap args, String taskType) throws TechnicalException {
		GenericMap result;
		switch (taskType) {
		case "uml":
			try {
				return new GenericValue(ResultType.SVG, plantuml(bean, args));
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
		case "render":
			try {
				result = renderOrExecute(null, bean, args, new GenericMap(), true);
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
			break;
		case "execute":
			try {
				result = renderOrExecute(null, bean, args, new GenericMap(), false);
			} catch (TechnicalNotFoundException e) {
				logger.error("Error {}", e);
				throw new TechnicalException(e);
			}
			break;
		default:
			result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * render plantuml
	 * @param sb 
	 * @param plugin 
	 * @return StringBuilder
	 * @throws TechnicalNotFoundException 
	 */
	public StringBuilder loadFromChildren(StringBuilder sb, ScriptPluginBean plugin) throws TechnicalNotFoundException {
		for (GenericEntity entity : sort(apiHrefPluginCommandResources.findAll(plugin), "order")) {
			CommandBean command = apiCommandResources.doGetByIdBean(entity.id);
			sb.append(MessageFormat.format("\"{0}\" -> \"{1}\" : appel\n", plugin.name, command.name, entity.get("type")));
			sb.append(MessageFormat.format("activate \"{0}\"\n", command.name));
			sb.append(MessageFormat.format("\"{0}\" <-- \"{1}\" : {2}\n", plugin.name, command.name, entity.get("name")));
			sb.append(MessageFormat.format("deactivate \"{0}\"\n", command.name));
			sb.append(MessageFormat.format("note right\n", 0));
			sb.append(MessageFormat.format("Mode: {0}\n", entity.get("type")));
			sb.append(MessageFormat.format("____\n", 0));
			sb.append(MessageFormat.format("Type: {0}\n", command.type.toString()));
			sb.append(MessageFormat.format("____\n", 0));
			sb.append(MessageFormat.format("{0}\n", command.body));
			sb.append(MessageFormat.format("end note\n", 0));
		}
		return sb;
	}

	/**
	 * render plantuml
	 * 
	 * @param bean
	 * @param args
	 * @return
	 * @throws TechnicalNotFoundException
	 */
	private String plantuml(ScriptPluginBean plugin, GenericMap args) throws TechnicalNotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append("@startuml\n");
		sb.append("autonumber\n");
		loadFromChildren(sb, plugin);
		sb.append("@enduml\n");
		return svg(sb.toString());
	}

	/**
	 * render all data command of this script as a pipeline
	 * 
	 * @param device
	 * @param plugin
	 *            the script to render
	 * @param args
	 *            arguments for this script
	 * @return GenericMap a result as a generic map
	 * @throws TechnicalNotFoundException
	 *             if not found
	 */
	public GenericMap render(DeviceBean device, ScriptPluginBean plugin, GenericMap args)
			throws TechnicalNotFoundException {
		return renderOrExecute(device, plugin, args, new GenericMap(), true);
	}

	/**
	 * execute all action command of this script as a pipeline
	 * 
	 * @param device
	 * @param plugin
	 *            the script to render
	 * @param args
	 *            arguments for this script
	 * @return GenericMap a result as a generic map
	 * @throws TechnicalNotFoundException
	 *             if not found
	 */
	public GenericMap execute(DeviceBean device, ScriptPluginBean plugin, GenericMap args)
			throws TechnicalNotFoundException {
		return renderOrExecute(device, plugin, args, new GenericMap(), false);
	}

	/**
	 * execute all command of this script as a pipeline
	 * 
	 * @param script
	 * @param args
	 * @param output
	 * @return GenericMap
	 * @throws TechnicalNotFoundException
	 */
	private GenericMap renderOrExecute(DeviceBean device, ScriptPluginBean plugin, GenericMap args, GenericMap output,
			boolean render) throws TechnicalNotFoundException {
		GenericMap result = args;
		int index = 0;
		for (GenericEntity entity : sort(apiHrefPluginCommandResources.findAll(plugin), "order")) {
			/**
			 * ignore data in phase action
			 */
			if (entity.get("type") != null && entity.get("type").equals("data") && !render) {
				logger.debug("Device {} plugin {} command {} cannot be executed, it's a data", device, plugin, entity);
				continue;
			}
			/**
			 * ignore action in phase data
			 */
			if (entity.get("type") != null && entity.get("type").equals("action") && render) {
				logger.debug("Device {} plugin {} command {} cannot be rendered, it's an action", device, plugin,
						entity);
				continue;
			}

			/**
			 * extract parameter
			 */
			if (entity.get("parameter") != null) {
				try {
					GenericMap map = mapper.readValue(entity.get("parameter").toString(), GenericMap.class);
					for (Entry<String, Object> field : map.entrySet()) {
						result.put(field.getKey(), field.getValue());
					}
				} catch (IOException e) {
					logger.warn("While decoding {}", entity.get("parameter").toString());
				}
			}

			/**
			 * retrieve command to execute
			 */
			CommandRest command = apiCommandResources.doGetByIdRest(entity.id);
			logger.info("Before render params = {}, context = {}", command, result);
			result = apiCommandResources.execute(device, plugin,
					mapperFactory.getMapperFacade().map(command, CommandBean.class), result);
			logger.info("After render params = {}, context = {}", command, result);

			/**
			 * store result in output
			 */
			if (entity.get("name") != null) {
				output.put((String) entity.get("name"), result);
			} else {
				output.put("field" + (index++), result);
			}
		}
		return output;
	}

	/**
	 * render as svg
	 * 
	 * @param payload
	 * @return String
	 * @throws TechnicalException
	 */
	private String svg(String payload) throws TechnicalException {
		SourceStringReader reader = new SourceStringReader(payload);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		try {
			reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		// The XML is stored into svg
		final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
		return svg;
	}
}
