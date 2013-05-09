package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlRandom;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlRandomImpl extends AimlElementContainer implements IAimlRandom {

	public AimlRandomImpl() {
		super("random");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
