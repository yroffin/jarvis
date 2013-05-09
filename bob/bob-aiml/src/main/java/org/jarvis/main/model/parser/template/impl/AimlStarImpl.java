package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlStar;

public class AimlStarImpl extends AimlElementContainer implements IAimlStar {

	public AimlStarImpl() {
		super("star");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlStar [elements=" + elements + "]";
	}
}
