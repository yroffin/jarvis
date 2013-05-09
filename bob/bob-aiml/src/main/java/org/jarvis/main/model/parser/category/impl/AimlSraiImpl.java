package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlSrai;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlSraiImpl extends AimlElementContainer implements IAimlSrai {

	public AimlSraiImpl() {
		super("srai");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSrai [elements=" + elements + "]";
	}
}
