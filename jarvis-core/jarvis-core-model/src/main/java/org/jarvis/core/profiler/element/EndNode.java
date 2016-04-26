package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * End node
 */
public class EndNode extends DefaultNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public EndNode(String name, String description) {
		super(name, description);
	}

	/**
	 * @return boolean
	 */
	public boolean isEnd() {
		return true;
	}

	@Override
	public String getLongId() {
		return "end#" + id;
	}
}
