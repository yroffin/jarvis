/**
 * 
 */
package org.jarvis.core.resources.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * default annotation to declare resource
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Declare {

	/**
	 * resource declaration
	 * @return String
	 */
	String resource();

	/**
	 * resource summary
	 * @return String
	 */
	String summary();

	/**
	 * rest class
	 * @return Class<?>
	 */
	Class<?> rest();

}
