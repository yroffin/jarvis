package org.jarvis.main.model.parser.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;

public class AimlData extends AimlElementContainer {

	private static final CharSequence	STAR	= "*";
	private final String				value;

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
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) {
		if (star.size() > 0) {
			/**
			 * * is only replace by first star element use <star /> with index
			 * for alternate star element
			 */
			render.append(value.replace(STAR, star.get(0)));
		} else {
			render.append(value);
		}
		return render;
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append(value);
		return render;
	}
}
