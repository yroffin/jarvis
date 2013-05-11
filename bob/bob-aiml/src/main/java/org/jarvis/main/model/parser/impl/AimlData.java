package org.jarvis.main.model.parser.impl;

public class AimlData extends AimlElementContainer {

	private final String	value;

	/**
	 * default constructor
	 * 
	 * @param value
	 */
	public AimlData(String value) {
		super("PCDATA");
		this.value = value;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tIAimlData ["
				+ value.replace("\n", "").replace("\r", "") + "]";
	}

	@Override
	public void answer(String star, String that, StringBuilder render) {
		render.append(value.replace("*", star).trim());
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append(value);
		return render;
	}
}
