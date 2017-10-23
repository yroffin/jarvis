package org.jarvis.core.resources.api;

/**
 * default interface for resource
 */
public interface ApiDefaultResources {

	/**
	 * declare a resource
	 * @param resource
	 */
	void declare(String resource);

	/**
	 * mount resource
	 */
	void mount();

	/**
	 * retrieve rest class
	 * @return Class<?>
	 */
	Class<?> getRestClass();

}
