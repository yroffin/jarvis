package org.jarvis.main.model.impl.parser.template.format;

import org.jarvis.main.model.impl.parser.template.AimlFormatContainer;
import org.jarvis.main.model.parser.template.format.IAimlFormal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * formal
 */
public class AimlFormalImpl extends AimlFormatContainer implements IAimlFormal {

	protected Logger logger = LoggerFactory.getLogger(AimlFormalImpl.class);

	/**
	 * constructor
	 */
	public AimlFormalImpl() {
		super("formal");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlFormal [elements=" + elements + "]";
	}
}
