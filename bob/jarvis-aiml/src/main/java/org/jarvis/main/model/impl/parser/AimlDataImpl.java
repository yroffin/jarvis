package org.jarvis.main.model.impl.parser;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class AimlDataImpl extends AimlElementContainer {

	private static final CharSequence STAR = "*";
	private final String value;

	private boolean starTransformation = true;

	/**
	 * default constructor
	 * 
	 * @param value
	 */
	public AimlDataImpl(String value) {
		super("PCDATA");
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * alternate constructor
	 * 
	 * @param value
	 * @param starTransformation
	 */
	public AimlDataImpl(String value, boolean starTransformation) {
		super("PCDATA");
		this.value = value;
		this.starTransformation = starTransformation;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tIAimlData ["
				+ value.replace("\n", "").replace("\r", "") + "]";
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) {
		if (star.size() > 0 && starTransformation) {
			/**
			 * * is only replace by first star element use <star /> with index
			 * for alternate star element
			 */
			render.append(value.replace(STAR, star.get(0)));
		} else {
			render.append(value.replace("\n", "").replace("\r", ""));
		}
		return render;
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append(value);
		return render;
	}
}
