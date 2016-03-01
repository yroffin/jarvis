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

package org.jarvis.core.services.shell;

import java.io.IOException;
import java.util.TreeMap;

import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.type.GenericMap;
import org.jarvis.runtime.ProcessExec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
@PropertySource("classpath:server.properties")
public class PluginShellService {

	@Autowired
	Environment env;

	/**
	 * @param command
	 * @param args 
	 * @return GenericMap
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public GenericMap command(GenericMap command, GenericMap args) throws IOException, InterruptedException {
		String result = ProcessExec.execute((String) command.get("body"));
		return (GenericMap) new TreeMap<String,Object>();
	}

	/**
	 * @param command
	 * @param args 
	 * @return GenericMap
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public GenericMap shell(GenericMap command, GenericMap args) throws IOException, InterruptedException {
		String result = ProcessExec.execute((String) command.get("body"));
		return (GenericMap) new TreeMap<String,Object>();
	}
}
