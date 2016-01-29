package org.jarvis.core.resources.api;

/**
 * listener pattern
 * @param <T> 
 */
public interface ResourceListener<T> {

	/**
	 * @param node
	 * @throws InterruptedException 
	 */
	void post(T node) throws InterruptedException;

}
