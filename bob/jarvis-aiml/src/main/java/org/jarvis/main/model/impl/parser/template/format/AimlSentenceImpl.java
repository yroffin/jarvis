package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
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
