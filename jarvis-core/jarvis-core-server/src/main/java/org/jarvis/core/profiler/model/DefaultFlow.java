package org.jarvis.core.profiler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * default node
 */
public abstract class DefaultFlow implements Comparable<DefaultFlow>, GenericNode {

	static protected int sequence;
	protected String id;
	protected String name;
	protected GenericNode sourceRef;
	protected GenericNode targetRef;
	private boolean bool;

	/**
	 * @param name
	 * @param target 
	 * @param source 
	 */
	public DefaultFlow(String name, GenericNode source, GenericNode target) {
		this.id = (sequence++)+"";
		this.name = name;
		this.sourceRef = source;
		this.targetRef = target;
	}

	/**
	 * @param name
	 * @param source
	 * @param target
	 * @param b
	 */
	public DefaultFlow(String name, GenericNode source, GenericNode target, boolean b) {
		this.id = (sequence++)+"";
		this.name = name;
		this.sourceRef = source;
		this.targetRef = target;
		this.setBool(b);
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return GenericNode
	 */
	@JsonIgnore
	public GenericNode getSourceRef() {
		return sourceRef;
	}

	/**
	 * @return GenericNode
	 */
	@JsonIgnore
	public GenericNode getTargetRef() {
		return targetRef;
	}

	@Override
	@JsonIgnore
	public abstract int getSorter();

	@Override
	public int compareTo(DefaultFlow o) {
		return (int) (getSorter() - o.getSorter());
	}

	/**
	 * @return String
	 */
	public String getSourceId() {
		return sourceRef.getId();
	}

	/**
	 * @return String
	 */
	public String getTargetId() {
		return targetRef.getId();
	}

	/**
	 * @return boolean
	 */
	public boolean isBool() {
		return bool;
	}

	/**
	 * @param bool
	 */
	public void setBool(boolean bool) {
		this.bool = bool;
	}

	@Override
	public String getDescription() {
		return "sequence flow";
	}
}
