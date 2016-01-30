package org.jarvis.core.model.bean.scenario;

import org.jarvis.core.model.bean.GenericBean;

/**
 * block
 */
public class BlockBean extends GenericBean {
	/**
	 * name
	 */
	public String name;
	/**
	 * icon
	 */
	public String icon;
	/**
	 * expression
	 */
	public String expression;

	@Override
	public String toString() {
		return "BlockBean [name=" + name + ", icon=" + icon + ", expression=" + expression + "]";
	}
}
