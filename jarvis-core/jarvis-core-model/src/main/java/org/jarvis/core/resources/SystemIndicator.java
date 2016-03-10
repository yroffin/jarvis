package org.jarvis.core.resources;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * simple indicators
 */
public class SystemIndicator {
	@JsonProperty
	protected long committedVirtualMemorySize;
	@JsonProperty
	protected long freePhysicalMemorySize;
	@JsonProperty
	protected long freeSwapSpaceSize;
	@JsonProperty
	protected double processCpuLoad;
	@JsonProperty
	protected long processCpuTime;
	@JsonProperty
	protected double systemCpuLoad;
	@JsonProperty
	protected long totalPhysicalMemorySize;
	@JsonProperty
	protected long totalSwapSpaceSize;

	/**
	 * constructor
	 * @param committedVirtualMemorySize
	 * @param freePhysicalMemorySize
	 * @param freeSwapSpaceSize
	 * @param processCpuLoad
	 * @param processCpuTime
	 * @param systemCpuLoad
	 * @param totalPhysicalMemorySize
	 * @param totalSwapSpaceSize
	 */
	public SystemIndicator(
			Object committedVirtualMemorySize, 
			Object freePhysicalMemorySize, 
			Object freeSwapSpaceSize, 
			Object processCpuLoad,
			Object processCpuTime, 
			Object systemCpuLoad,
			Object totalPhysicalMemorySize, 
			Object totalSwapSpaceSize) { 
		this.committedVirtualMemorySize = (long) committedVirtualMemorySize;
		this.freePhysicalMemorySize = (long) freePhysicalMemorySize;
		this.freeSwapSpaceSize = (long) freeSwapSpaceSize;
		this.processCpuLoad = (double) processCpuLoad;
		this.processCpuTime = (long) processCpuTime;
		this.systemCpuLoad = (double) systemCpuLoad;
		this.totalPhysicalMemorySize = (long) totalPhysicalMemorySize;
		this.totalSwapSpaceSize = (long) totalSwapSpaceSize;
	}

	private static MBeanServer mbs = null;
	private static ObjectName name = null;
	
	/**
	 * init this bean
	 */
	public static void init() {
		mbs = ManagementFactory.getPlatformMBeanServer();
	    name = null;
		try {
			name = ObjectName.getInstance(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
		} catch (MalformedObjectNameException | NullPointerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * build new indicator
	 * @return SystemIndicator
	 */
	public static SystemIndicator factory() {
	    AttributeList list = null;
		try {
			list = mbs.getAttributes(name, new String[]{ "CommittedVirtualMemorySize", "FreePhysicalMemorySize", "FreeSwapSpaceSize", "ProcessCpuLoad", "ProcessCpuTime", "SystemCpuLoad", "TotalPhysicalMemorySize", "TotalSwapSpaceSize" });
		} catch (InstanceNotFoundException | ReflectionException e) {
			throw new RuntimeException(e);
		}
		return new SystemIndicator(
				((Attribute) list.get(0)).getValue(),
				((Attribute)list.get(1)).getValue(),
				((Attribute)list.get(2)).getValue(),
				((Attribute)list.get(3)).getValue(),
				((Attribute)list.get(4)).getValue(),
				((Attribute)list.get(5)).getValue(),
				((Attribute)list.get(6)).getValue(),
				(((Attribute)list.get(7)).getValue())
		);
	}
}
