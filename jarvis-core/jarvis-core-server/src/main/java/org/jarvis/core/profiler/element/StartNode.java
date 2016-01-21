package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultSorterNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Start node
 */
public class StartNode extends DefaultSorterNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public StartNode(String name, String description) {
		super(name, description);
	}

	/**
	 * @return boolean
	 */
	public boolean isStart() {
		return true;
	}
}
