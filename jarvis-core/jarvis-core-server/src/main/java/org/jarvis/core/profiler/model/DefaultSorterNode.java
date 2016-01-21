package org.jarvis.core.profiler.model;

/**
 * default node
 */
public abstract class DefaultSorterNode extends DefaultNode implements Comparable<GenericNode>, GenericNode {

	/**
	 * @param name
	 * @param description 
	 */
	public DefaultSorterNode(String name, String description) {
		super(name, description);
	}

	@Override
	public int getSorter() {
		return 0;
	}
}
