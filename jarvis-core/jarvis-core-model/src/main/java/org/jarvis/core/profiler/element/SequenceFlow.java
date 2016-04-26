package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultFlow;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Edge
 */
public class SequenceFlow extends DefaultFlow implements Comparable<DefaultFlow>, GenericNode {

	/**
	 * @param name
	 * @param target 
	 * @param source 
	 */
	public SequenceFlow(String name, GenericNode source, GenericNode target) {
		super(name, source, target);
	}

	/**
	 * @param name
	 * @param source
	 * @param target
	 * @param b
	 */
	public SequenceFlow(String name, GenericNode source, GenericNode target, boolean b) {
		super(name, source, target, b);
	}

	@Override
	public String getSorter() {
		return id;
	}

	@Override
	public String getLongId() {
		return "flow#" + id;
	}
}
