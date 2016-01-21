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

import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.type.GenericMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * main daemon
 */
@Component
@PropertySource("classpath:server.properties")
public class PluginGroovyService {
	protected Logger logger = LoggerFactory.getLogger(PluginGroovyService.class);

	@Autowired
	Environment env;

	private Binding binding;

	private GroovyShell script;

	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		binding = new Binding();
		script = new GroovyShell(binding);
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	public GenericMap groovy(GenericMap command, GenericMap args) throws TechnicalException {
		return groovyAsObject((String) command.get("body"), args);
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	public GenericMap groovyAsObject(String command, GenericMap args) throws TechnicalException {
		binding.setVariable("input", args);
		@SuppressWarnings("rawtypes")
		Map exec = (Map) script.evaluate(command);
		GenericMap result = new GenericMap();
		if(exec != null) {
			for(Object key : exec.keySet()) {
				result.put((String) key, exec.get(key));
			}
		} else {
			logger.warn("SCRIPT - output is null");
		}
		return result;
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws TechnicalException 
	 */
	public boolean groovyAsBoolean(String command, GenericMap args) throws TechnicalException {
		Binding binding = new Binding();
		binding.setVariable("input", args);
		GroovyShell script = new GroovyShell(binding);
		return (boolean) script.evaluate(command);
	}
}
