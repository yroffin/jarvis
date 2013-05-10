package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlRandom;

public class AimlRandomImpl extends AimlElementContainer implements IAimlRandom {

	public AimlRandomImpl() {
		super("random");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
