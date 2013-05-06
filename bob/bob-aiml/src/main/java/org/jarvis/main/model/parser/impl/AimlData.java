package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlData;

public class AimlData implements IAimlData {

	private String value;

	/**
	 * default constructor
	 * 
	 * @param value
	 */
	public AimlData(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tIAimlData ["
				+ value.replace("\n", "").replace("\r", "") + "]";
	}

}
