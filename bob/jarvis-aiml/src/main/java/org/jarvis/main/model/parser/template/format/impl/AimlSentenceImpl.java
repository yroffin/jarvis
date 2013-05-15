package org.jarvis.main.model.parser.template.format.impl;

import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.format.IAimlSentence;

public class AimlSentenceImpl extends AimlElementContainer implements
		IAimlSentence {

	public AimlSentenceImpl() {
		super("sentence");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSentence [elements=" + elements + "]";
	}
}
