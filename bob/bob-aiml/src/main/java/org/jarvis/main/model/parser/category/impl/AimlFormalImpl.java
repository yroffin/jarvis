package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlFormal;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlFormalImpl extends AimlElementContainer implements IAimlFormal {

	public AimlFormalImpl() {
		super("formal");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
