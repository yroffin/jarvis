package org.jarvis.core.resources.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Linked resource
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DeclareLinkedResource {

	/**
	 * role
	 * @return String
	 */
	String role();

	/**
	 * param
	 * @return String
	 */
	String param();

	/**
	 * sortKey
	 * @return String
	 */
	String sortKey();

}
