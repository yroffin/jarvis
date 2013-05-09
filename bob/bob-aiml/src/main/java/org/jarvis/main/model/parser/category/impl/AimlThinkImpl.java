package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlThinkImpl extends AimlElementContainer implements IAimlThink {

	public AimlThinkImpl() {
		super("think");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
