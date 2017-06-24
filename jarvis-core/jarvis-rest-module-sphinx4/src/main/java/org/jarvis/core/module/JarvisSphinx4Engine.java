/**
 *  Copyright 2017 Yannick Roffin
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

import javax.annotation.PostConstruct;

import org.jarvis.core.services.CoreRestDaemon;
import org.jarvis.core.services.JarvisConnector;
import org.jarvis.core.services.JarvisConnectorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * voide module
 */
@Component
public class JarvisSphinx4Engine extends JarvisConnectorImpl implements JarvisConnector {

	@Autowired
	Environment env;

	@Autowired
	CoreRestDaemon daemon;
	
	protected Logger logger = LoggerFactory.getLogger(JarvisSphinx4Engine.class);

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
		renderer = false;
		
		/**
		 * notify
		 */
		daemon.notify(this);
	}
}
