package org.jarvis.main.model.impl.parser.template.trans;

import org.jarvis.main.model.impl.parser.template.AimlFormatContainer;
import org.jarvis.main.model.parser.template.trans.IAimlPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * person
 */
public class AimlPersonImpl extends AimlFormatContainer implements IAimlPerson {

	protected Logger logger = LoggerFactory.getLogger(AimlPersonImpl.class);

	/**
	 * constructor
	 */
	public AimlPersonImpl() {
		super("person");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
