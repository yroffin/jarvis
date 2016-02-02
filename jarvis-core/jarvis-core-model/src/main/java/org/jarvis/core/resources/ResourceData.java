package org.jarvis.core.resources;

/**
 * resource
 */
public class ResourceData {
	/**
	 * @param value
	 */
	public ResourceData(String value) {
		this.setValue(value);
	}
	/**
	 * @return String
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return String
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	private String value;
	private String contentType;
}
