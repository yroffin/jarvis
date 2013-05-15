package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlBr;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlBrImpl extends AimlElementContainer implements IAimlBr {

	public AimlBrImpl() {
		super("Br");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlBr [elements=" + elements + "]";
	}
}
