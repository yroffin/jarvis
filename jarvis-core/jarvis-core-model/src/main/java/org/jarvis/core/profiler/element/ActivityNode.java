package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Start node
 */
public class ActivityNode extends DefaultNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public ActivityNode(String name, String description) {
		super(name, description);
	}

	/**
	 * @return boolean
	 */
	public boolean isActivity() {
		return true;
	}

	@Override
	public String getLongId() {
		return "activity#" + id;
	}
}
