package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Gateway node
 */
public class BooleanGatewayNode extends DefaultNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public BooleanGatewayNode(String name, String description) {
		super(name, description);
	}

	/**
	 * @return boolean
	 */
	public boolean isGateway() {
		return true;
	}

	@Override
	public String getLongId() {
		return "gateway#" + id;
	}
}
