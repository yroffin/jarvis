package org.jarvis.core.profiler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * generic node interface
 */
@JsonPropertyOrder(alphabetic=true)
public interface GenericNode {
	/**
	 * node type
	 */
	public enum NodeType {
		/**
		 * start
		 */
		START,
		/**
		 * stop
		 */
		END,
		/**
		 * simple activity
		 */
		ACTIVITY,
		/**
		 * call activity behaviour
		 */
		CALL_ACTIVITY,
		/**
		 * gateway
		 */
		BOOLEAN_GATEWAY,
		/**
		 * SEQUENCE_FLOW
		 */
		SEQUENCE_FLOW
	}

	/**
	 * @return String
	 */
	String getId();

	/**
	 * @return String
	 */
	String getName();

	/**
	 * @return int
	 */
	@JsonIgnore
	String getSorter();

	/**
	 * @return String
	 */
	String getDescription();

	/**
	 * @return String
	 */
	String getLongId();
}
