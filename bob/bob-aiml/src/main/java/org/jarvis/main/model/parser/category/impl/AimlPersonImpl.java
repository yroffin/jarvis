package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlPerson;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlPersonImpl extends AimlElementContainer implements IAimlPerson {

	public AimlPersonImpl() {
		super("person");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
