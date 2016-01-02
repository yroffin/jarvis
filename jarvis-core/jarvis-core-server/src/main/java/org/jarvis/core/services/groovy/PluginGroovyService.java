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

import java.io.IOException;
import java.util.Map.Entry;
import org.jarvis.core.type.GenericMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import groovy.json.internal.LazyMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/**
 * main daemon
 */
@Component
@PropertySource("classpath:server.properties")
public class PluginGroovyService {

	@Autowired
	Environment env;

	/**
	 * @param command
	 * @param args 
	 * @return GenericBean
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public GenericMap groovy(GenericMap command, GenericMap args) throws IOException, InterruptedException {
		Binding binding = new Binding();
		binding.setVariable("input", args);
		GroovyShell script = new GroovyShell(binding);
		LazyMap exec = (LazyMap) script.evaluate((String) command.get("body"));
		GenericMap result = new GenericMap();
		for(Entry<String, Object> set : exec.entrySet()) {
			result.put(set.getKey(), set.getValue());
		}
		return result;
	}
}
