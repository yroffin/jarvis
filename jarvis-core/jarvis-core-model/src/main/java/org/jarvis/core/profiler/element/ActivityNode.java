package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultSorterNode;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Start node
 */
public class ActivityNode extends DefaultSorterNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param descriptione 
	 */
	public ActivityNode(String name, String descriptione) {
		super(name, descriptione);
	}

	/**
	 * @return boolean
	 */
	public boolean isActivity() {
		return true;
	}
}
