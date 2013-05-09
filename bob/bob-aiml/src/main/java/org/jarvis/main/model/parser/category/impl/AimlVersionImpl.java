package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlVersion;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlVersionImpl extends AimlElementContainer implements IAimlVersion {

	public AimlVersionImpl() {
		super("version");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlVersion [elements=" + elements + "]";
	}
}
