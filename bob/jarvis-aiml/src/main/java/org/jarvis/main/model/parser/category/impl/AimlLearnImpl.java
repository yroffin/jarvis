package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlLearn;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlLearnImpl extends AimlElementContainer implements IAimlLearn {

	public AimlLearnImpl() {
		super("think");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLearn [elements=" + elements + "]";
	}
}
