package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultSorterNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Start node
 */
public class BooleanGatewayNode extends DefaultSorterNode implements Comparable<GenericNode>, GenericNode {

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
}
