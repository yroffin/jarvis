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
