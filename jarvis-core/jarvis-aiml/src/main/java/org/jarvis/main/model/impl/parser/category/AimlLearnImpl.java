package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.category.IAimlLearn;

public class AimlLearnImpl extends AimlElementContainer implements IAimlLearn {

	public AimlLearnImpl() {
		super("think");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLearn [elements=" + elements + "]";
	}
}
