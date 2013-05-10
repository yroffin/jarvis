package org.jarvis.main.model.parser.template.system.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlDate;

public class AimlSizeImpl extends AimlElementContainer implements IAimlDate {

	public AimlSizeImpl() {
		super("date");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlDate [elements=" + elements + "]";
	}
}
