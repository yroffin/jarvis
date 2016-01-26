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
	/**
	 * pluginId
	 */
	public String pluginId;
	/**
	 * pluginName
	 */
	public String pluginName;
	/**
	 * pluginThenId
	 */
	public String pluginThenId;
	/**
	 * pluginThenName
	 */
	public String pluginThenName;
	/**
	 * pluginElseId
	 */
	public String pluginElseId;
	/**
	 * pluginElseName
	 */
	public String pluginElseName;
	/**
	 * blockThenId
	 */
	public String blockThenId;
	/**
	 * blockThenName
	 */
	public String blockThenName;
	/**
	 * blockElseId
	 */
	public String blockElseId;
	/**
	 * blockElseName
	 */
	public String blockElseName;

	@Override
	public String toString() {
		return "BlockBean [name=" + name + ", icon=" + icon + ", expression=" + expression + ", pluginId=" + pluginId
				+ ", pluginName=" + pluginName + ", pluginThenId=" + pluginThenId + ", pluginThenName=" + pluginThenName
				+ ", pluginElseId=" + pluginElseId + ", pluginElseName=" + pluginElseName + ", blockThenId="
				+ blockThenId + ", blockThenName=" + blockThenName + ", blockElseId=" + blockElseId + ", blockElseName="
				+ blockElseName + "]";
	}
}
