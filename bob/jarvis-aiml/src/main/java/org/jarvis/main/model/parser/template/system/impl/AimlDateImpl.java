package org.jarvis.main.model.parser.template.system.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlDate;

public class AimlDateImpl extends AimlElementContainer implements IAimlDate {

	public AimlDateImpl() {
		super("date");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlDate [elements=" + elements + "]";
	}
}
