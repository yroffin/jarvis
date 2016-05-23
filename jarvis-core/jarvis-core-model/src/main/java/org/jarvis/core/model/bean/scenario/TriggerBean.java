package org.jarvis.core.model.bean.scenario;

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
	 * sunset
	 */
	public boolean sunset;
	/**
	 * sunrise
	 */
	public boolean sunrise;
	/**
	 * second
	 */
	public boolean second;
	/**
	 * minute
	 */
	public boolean minute;
	/**
	 * hour
	 */
	public boolean hour;
	/**
	 * day
	 */
	public boolean day;
	/**
	 * custom
	 */
	public boolean custom;
	/**
	 * crontab
	 */
	public String crontab;
}
