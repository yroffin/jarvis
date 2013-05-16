package org.jarvis.main.model.impl.parser.template;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlPerson2;

public class AimlPerson2Impl extends AimlElementContainer implements IAimlPerson2 {

	public AimlPerson2Impl() {
		super("person2");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlPerson2 [elements=" + elements + "]";
	}
}
