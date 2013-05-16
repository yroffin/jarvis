package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.format.IAimlUppercase;

public class AimlUppercaseImpl extends AimlElementContainer implements
		IAimlUppercase {

	public AimlUppercaseImpl() {
		super("uppercase");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlUppercase [elements=" + elements + "]";
	}
}
