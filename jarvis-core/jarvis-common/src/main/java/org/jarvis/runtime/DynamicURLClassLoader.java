package org.jarvis.runtime;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * dynamic classloader
 */
public class DynamicURLClassLoader extends URLClassLoader {
	/**
	 * @param classLoader
	 */
	public DynamicURLClassLoader(URLClassLoader classLoader) {
		super(classLoader.getURLs());

	}

	/**
	 * @param url
	 */
	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}
	
	/**
	 * @return DynamicURLClassLoader
	 */
	public static DynamicURLClassLoader getDynamicURLClassLoader() {
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		DynamicURLClassLoader dynalLoader = new DynamicURLClassLoader(urlClassLoader);
		
		Thread.currentThread().setContextClassLoader(dynalLoader);
		
		return dynalLoader;
	}
}
