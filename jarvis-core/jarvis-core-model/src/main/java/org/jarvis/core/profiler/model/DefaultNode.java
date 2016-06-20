package org.jarvis.core.profiler.model;

/**
 * default node
 */
public abstract class DefaultNode implements Comparable<GenericNode>, GenericNode {

	protected static int sequence = 10;
	protected String id;
	protected String name;
	protected String description;

	/**
	 * @param name
	 * @param description 
	 */
	public DefaultNode(String name, String description) {
		this.id = Integer.toString(sequence++);
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
	public String getSorter() {
		return id;
	}

	@Override
	public int compareTo(GenericNode o) {
		return (int) (getSorter().compareTo(o.getSorter()));
	}

	@Override
	public String toString() {
		return "DefaultNode [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
