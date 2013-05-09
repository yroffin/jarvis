package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlId;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlIdImpl extends AimlElementContainer implements IAimlId {

	public AimlIdImpl() {
		super("id");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlId [elements=" + elements + "]";
	}
}
