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

package org.jarvis.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GlobalConfig {
	private static GlobalConfig __instance = null;

	/**
	 * singleton pattern
	 * 
	 * @return
	 */
	public static GlobalConfig instance() {
		if (__instance != null)
			return __instance;
		else
			try {
				__instance = new GlobalConfig();
			} catch (IOException e) {
				e.printStackTrace();
			}
		return __instance;
	}

	/**
	 * internal constructor
	 * 
	 * @throws IOException
	 */
	private GlobalConfig() throws IOException {
		InputStream stream = this.getClass().getResourceAsStream(
				"/jarvis.properties");
		if (stream == null) {
			throw new RuntimeException(
					"Cannot find jarvis.properties in classpath");
		}
		/**
		 * load properties
		 */
		prop.load(stream);
		System.out.println("prop");
	}

	private static Properties prop = new Properties();

	private String getProperty(String key, String def) {
		if (prop.getProperty(key) != null)
			return prop.getProperty(key);
		else
			return def;
	}

	private int getProperty(String key, int def) {
		if (prop.getProperty(key) != null)
			return Integer.parseInt(prop.getProperty(key));
		else
			return def;
	}

	public String getJarvisHost() {
		return getProperty("jarvis.srv.host", "localhost");
	}

	public int getJarvisLsnPort() {
		return getProperty("jarvis.srv.listener.port", 5000);
	}
}
