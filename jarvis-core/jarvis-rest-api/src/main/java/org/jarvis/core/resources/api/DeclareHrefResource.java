package org.jarvis.core.resources.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  declare href resource
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DeclareHrefResource {

	/**
	 * role of this resource
	 * @return String
	 */
	String role();

	/**
	 * href
	 * @return String
	 */
	String href();

}
