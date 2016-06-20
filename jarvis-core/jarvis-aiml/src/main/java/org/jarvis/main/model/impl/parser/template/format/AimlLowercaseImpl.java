package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.template.AimlFormatAltContainer;
import org.jarvis.main.model.parser.template.format.IAimlLowercase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * lowercase
 */
public class AimlLowercaseImpl extends AimlFormatAltContainer implements
		IAimlLowercase {

	protected Logger logger = LoggerFactory.getLogger(AimlLowercaseImpl.class);

	/**
	 * constructor
	 */
	public AimlLowercaseImpl() {
		super("lowercase");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLowercase [elements=" + elements + "]";
	}
}
