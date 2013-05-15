package org.jarvis.main.model.parser.template.system.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlId;

public class AimlIdImpl extends AimlElementContainer implements IAimlId {

	public AimlIdImpl() {
		super("id");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlId [elements=" + elements + "]";
	}
}
