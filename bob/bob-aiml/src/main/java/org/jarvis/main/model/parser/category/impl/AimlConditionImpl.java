package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlCondition;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlConditionImpl extends AimlElementContainer implements IAimlCondition {

	public AimlConditionImpl() {
		super("condition");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCondition [elements=" + elements + "]";
	}
}
