package org.jarvis.main.model.parser.template.system.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
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
