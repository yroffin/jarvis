package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.template.AimlFormatContainer;
import org.jarvis.main.model.parser.template.format.IAimlSentence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sentence
 */
public class AimlSentenceImpl extends AimlFormatContainer implements
		IAimlSentence {

	protected Logger logger = LoggerFactory.getLogger(AimlSentenceImpl.class);

	/**
	 * constructor
	 */
	public AimlSentenceImpl() {
		super("sentence");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSentence [elements=" + elements + "]";
	}
}
