package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlStar;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlStarImpl extends AimlElementContainer implements IAimlStar {

	public AimlStarImpl() {
		super("star");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlStar [elements=" + elements + "]";
	}
}
