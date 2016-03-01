package org.jarvis.core.profiler.element;

import org.jarvis.core.profiler.model.DefaultFlow;
import org.jarvis.core.profiler.model.GenericNode;

/**
 * Start node
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
	public int getSorter() {
		return 0;
	}
}
