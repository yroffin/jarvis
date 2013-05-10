package org.jarvis.main.model.parser.template.format.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.format.IAimlFormal;

public class AimlFormalImpl extends AimlElementContainer implements IAimlFormal {

	public AimlFormalImpl() {
		super("formal");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlFormal [elements=" + elements + "]";
	}
}
