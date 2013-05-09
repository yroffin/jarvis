package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlLiImpl extends AimlElementContainer implements IAimlLi {

	public AimlLiImpl() {
		super("li");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
