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
package org.jarvis.core;

import org.jarvis.core.module.JarvisDioEngine;
import org.jarvis.core.services.CoreRestDaemon;
import org.jarvis.core.services.ModuleThreadPoolTaskScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * simple bootstrap for rest bootstrap
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(
	    basePackages = {"org.jarvis.core.module, org.jarvis.core.services"}, 
	    useDefaultFilters = false,
	    includeFilters = {
	        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JarvisDioEngine.class),
	        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ModuleThreadPoolTaskScheduler.class),
	        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CoreRestDaemon.class)
})
public class JarvisCoreModule {
	protected static String normalizedPath = JarvisStatic.normalizedPath;

	/**
	 * main entry
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(JarvisCoreModule.class, args);
	}
}
