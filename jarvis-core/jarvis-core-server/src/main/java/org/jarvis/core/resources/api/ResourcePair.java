package org.jarvis.core.resources.api;

import java.util.Map;

import org.jarvis.core.type.ResultType;

/**
 * pair
 */
public class ResourcePair implements Map.Entry<ResultType, String> {

	private ResultType key;
	private String value;

	/**
	 * @param key
	 * @param value
	 */
	public ResourcePair(ResultType key, String value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public ResultType getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		this.value = value;
		return value;
	}
	
}
