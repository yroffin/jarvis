package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.category.IAimlLi;

public class AimlLiImpl extends AimlElementContainer implements IAimlLi {

	public AimlLiImpl() {
		super("li");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
