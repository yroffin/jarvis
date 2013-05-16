package org.jarvis.main.model.impl.parser.template.system;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlSystem;

public class AimlSystemImpl extends AimlElementContainer implements IAimlSystem {

	public AimlSystemImpl() {
		super("system");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSystem [elements=" + elements + "]";
	}
}
