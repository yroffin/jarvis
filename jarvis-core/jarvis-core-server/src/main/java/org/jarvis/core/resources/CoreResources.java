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

package org.jarvis.core.resources;

import org.jarvis.core.services.cache.CoreEhcacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * core resources (ui, ...)
 */
@Component
public class CoreResources {
	protected Logger logger = LoggerFactory.getLogger(CoreResources.class);

	@Autowired
	Environment env;

	@Autowired
	CoreEhcacheManager coreEhcacheManager;

	/**
	 * mount local resource
	 */
	public void mountLocal() {
		logger.info("Mount local files on {}", "public");
		spark.Spark.staticFiles.location("/public");
		spark.Spark.staticFiles.externalLocation("public");
	}

	/**
	 * mount local resource
	 */
	public void mountExternal() {
		/**
		 * mount resources
		 */
		spark.Spark.staticFiles.location("/public");
	}
}
