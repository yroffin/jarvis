package org.jarvis.core.profiler.model;

/**
 * default node
 */
public abstract class DefaultNode implements Comparable<GenericNode>, GenericNode {

	@Override
	public String toString() {
		return "DefaultNode [id=" + id + ", name=" + name + ", description=" + description + "]";
	}

	static protected int sequence;
	protected String id;
	protected String name;
	protected String description;

	/**
	 * @param name
	 * @param description 
	 */
	public DefaultNode(String name, String description) {
		this.id = (sequence++)+"";
		this.name = name;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract int getSorter();

	@Override
	public int compareTo(GenericNode o) {
		return (int) (getSorter() - o.getSorter());
	}
}
