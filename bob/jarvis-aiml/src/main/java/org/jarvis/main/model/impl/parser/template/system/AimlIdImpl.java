package org.jarvis.main.model.impl.parser.template.system;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
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
