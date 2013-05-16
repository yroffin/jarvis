package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
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
