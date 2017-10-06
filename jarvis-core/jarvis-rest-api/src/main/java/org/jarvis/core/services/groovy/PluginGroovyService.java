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

package org.jarvis.core.services.groovy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.common.core.exception.TechnicalException;
import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.config.PropertyBean;
import org.jarvis.core.resources.api.config.ApiPropertyResources;
import org.jarvis.core.services.PluginService;
import org.jarvis.core.services.helper.PluginConnectorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * main daemon
 */
@Component
public class PluginGroovyService extends PluginService {
	protected Logger logger = LoggerFactory.getLogger(PluginGroovyService.class);

	@Autowired
	Environment env;

	@Autowired
	private PluginConnectorHelper helper;

	@Autowired
	ApiPropertyResources apiPropertyResources;
	
	/**
	 * init
	 */
	@PostConstruct
	public void init() {
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public GenericMap asObject(GenericMap command, GenericMap args) throws TechnicalException {
		return groovyAsObject((String) command.get("body"), args);
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	@Override
	public boolean asBoolean(GenericMap command, GenericMap args) throws TechnicalException {
		return groovyAsBoolean((String) command.get("body"), args);
	}

	/**
	 * execute this command and return an object
	 * @param command
	 * the command to execute
	 * @param args
	 * arguments for this execution
	 * @return GenericBean
	 * the result
	 * @throws TechnicalException
	 * when some internal error 
	 */
	public GenericMap groovyAsObject(String command, GenericMap args) throws TechnicalException {
		/**
		 * declare helper in script
		 */
		Binding binding = new Binding();
		binding.setVariable("helper", helper);
		binding.setVariable("logger", logger);

		GroovyShell script = new GroovyShell(binding);

		/**
		 * store args in input bind field
		 * and all properties
		 */
		script.setVariable("input", args);
		for(PropertyBean key : apiPropertyResources.doFindAllBean()) {
			script.setVariable(key.key, key.value);
		}

		Object raw = script.evaluate(command);
		Map<?,?> exec = null;
		/**
		 * handle map result
		 */
		if(raw instanceof Map) {
			exec = (Map<?,?>) raw;
		}
		/**
		 * handle list result
		 */
		if(raw instanceof List) {
			exec = (Map<?,?>) ((List<?>) raw).get(0);
		}
		GenericMap result = new GenericMap();
		if(exec != null) {
			/**
			 * store all field in groovy context
			 */
			for(Object key : exec.keySet()) {
				result.put((String) key, exec.get(key));
			}
		} else {
			if(raw instanceof Boolean) {
				result.put((String) "result", raw);
			} else {
				logger.warn("SCRIPT - output is null");
			}
		}
		return result;
	}

	/**
	 * execute this command and return a bollean result
	 * @param command
	 * the command to execute
	 * @param args
	 * arguments for this execution
	 * @return boolean
	 * the result
	 * @throws TechnicalException
	 * when some internal error
	 */
	public boolean groovyAsBoolean(String command, GenericMap args) throws TechnicalException {
		/**
		 * declare helper in script
		 */
		Binding binding = new Binding();
		binding.setVariable("helper", helper);
		binding.setVariable("logger", logger);

		GroovyShell script = new GroovyShell(binding);

		for(Entry<String, Object> entry : args.entrySet()) {
			script.setVariable(entry.getKey(), entry.getValue());
		}

		/**
		 * map args to input and all properties
		 */
		script.setVariable("input", args);
		for(PropertyBean key : apiPropertyResources.doFindAllBean()) {
			script.setVariable(key.key, key.value);
		}


		boolean result = (boolean) script.evaluate(command);
		logger.warn("SCRIPT - BOOLEAN {}", result);
		return result;
	}
}
