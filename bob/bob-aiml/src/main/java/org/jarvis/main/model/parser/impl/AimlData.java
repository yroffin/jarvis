package org.jarvis.main.model.parser.impl;

public class AimlData extends AimlElementContainer {

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

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append(value);
		return render;
	}
}
