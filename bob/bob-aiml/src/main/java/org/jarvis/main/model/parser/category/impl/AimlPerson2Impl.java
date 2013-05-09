package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlPerson2;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlPerson2Impl extends AimlElementContainer implements IAimlPerson2 {

	public AimlPerson2Impl() {
		super("person2");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlPerson2 [elements=" + elements + "]";
	}
}
