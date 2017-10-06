package org.jarvis.core.model.bean.trigger;

import org.jarvis.core.model.bean.GenericBean;

/**
 * trigger
 */
public class TriggerBean extends GenericBean {
	/**
	 * name
	 */
	public String name;
	/**
	 * icon
	 */
	public String icon;
	/**
	 * linked topic
	 */
	public String topic;
	/**
	 * linked topic body
	 */
	public String body;
	@Override
	public String toString() {
		return "TriggerBean [name=" + name + ", icon=" + icon + ", topic=" + topic + ", body=" + body + "]";
	}
	
	
}
