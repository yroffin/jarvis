package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.format.IAimlLowercase;

public class AimlLowercaseImpl extends AimlElementContainer implements
		IAimlLowercase {

	public AimlLowercaseImpl() {
		super("lowercase");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLowercase [elements=" + elements + "]";
	}
}
