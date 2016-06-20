/**
 * Copyright 2015 Yannick Roffin
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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * simple class to init the system variable
 */
public class JarvisStatic {
	/**
	 * static CWD of this system
	 */
	public static String normalizedPath = getNormalizedPath();
	
	private static String getNormalizedPath() {
		/**
		 * set normalized path in properties
		 */
		Path absolutePath = Paths.get(".").toAbsolutePath().normalize();
		String prefix = absolutePath.getRoot().toString();
		String normalizedPath = absolutePath.toString().replace(prefix, "\\").replace("\\", "/");
		/**
		 * fix properties
		 */
		if(System.getProperty("jarvis.user.dir") == null) {
			System.setProperty("jarvis.user.dir", normalizedPath);
		}
		if(System.getProperty("jarvis.log.dir") == null) {
			System.setProperty("jarvis.log.dir", normalizedPath);
		}
		return normalizedPath;
	}
}
