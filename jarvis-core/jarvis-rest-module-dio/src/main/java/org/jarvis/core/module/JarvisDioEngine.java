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

package org.jarvis.core.module;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jarvis.core.services.CoreMethod;
import org.jarvis.core.services.CoreRestDaemon;
import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.jarvis.dio.DioHelper;
import org.jarvis.rest.services.impl.JarvisModuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * voide module
 */
@Component
public class JarvisDioEngine extends JarvisConnectorImpl implements JarvisConnector {

	@Autowired
	Environment env;

	@Autowired
	CoreRestDaemon daemon;
	
	protected Logger logger = LoggerFactory.getLogger(JarvisDioEngine.class);
	DioHelper dioHelper;

	/**
	 * constructor
	 */
	@PostConstruct
	public void init() {
		/**
		 * init default value
		 */
		super.init(env);
		
		/**
		 * is a renderer
		 */
		renderer = true;
		
		/**
		 * declare helper
		 */
		dioHelper = new DioHelper();
		
		/**
		 * notify
		 */
		daemon.notify(this);

		/**
		 * register api
		 */
		daemon.register(CoreMethod.POST, "/api/dio", this);
	}

	/**
	 * @param pin 
	 * @param sender 
	 * @param interruptor 
	 * @param on 
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws NumberFormatException 
	 */
	public void action(String pin, String sender, String interruptor, boolean on) throws IOException, NumberFormatException, InterruptedException {
		if(on) {
			dioHelper.pin(Integer.parseInt(pin)).sender(Integer.parseInt(sender)).interruptor(Integer.parseInt(interruptor)).switchOn();
		} else {
			dioHelper.pin(Integer.parseInt(pin)).sender(Integer.parseInt(sender)).interruptor(Integer.parseInt(interruptor)).switchOff();
		}
	}

	@Override
	public Map<String, Object> post(Map<String, Object> input, Map<String, String> params) throws JarvisModuleException {
		try {
			try {
				action((String) input.get("pin"), (String) input.get("sender"), (String) input.get("interruptor"), Boolean.parseBoolean((String) input.get("on")));
			} catch (NumberFormatException | InterruptedException e) {
				throw new IOException(e);
			}
		} catch (IOException e) {
			logger.error("Error, while accessing to jarvis with {} exception {}", input,
					e.getMessage());
			throw new JarvisModuleException(e);
		}
		return input;
	}
}
