package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Call activity node
 */
public class CallActivityNode extends DefaultNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public CallActivityNode(String name, String description) {
		super(name, description);
	}

	/**
	 * @return boolean
	 */
	public boolean isCall() {
		return true;
	}

	@Override
	public String getLongId() {
		return "call#" + id;
	}
}
