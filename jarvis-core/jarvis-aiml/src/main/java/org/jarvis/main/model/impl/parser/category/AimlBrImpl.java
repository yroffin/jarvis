package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.category.IAimlBr;

public class AimlBrImpl extends AimlElementContainer implements IAimlBr {

	public AimlBrImpl() {
		super("Br");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlBr [elements=" + elements + "]";
	}
}
