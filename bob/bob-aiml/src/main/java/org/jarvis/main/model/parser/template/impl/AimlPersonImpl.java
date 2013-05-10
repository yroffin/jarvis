package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlPerson;

public class AimlPersonImpl extends AimlElementContainer implements IAimlPerson {

	public AimlPersonImpl() {
		super("person");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
