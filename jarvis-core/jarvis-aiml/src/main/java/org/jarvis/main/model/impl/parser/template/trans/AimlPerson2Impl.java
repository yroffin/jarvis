package org.jarvis.main.model.impl.parser.template.trans;

import org.jarvis.main.model.impl.parser.template.AimlFormatAltContainer;
import org.jarvis.main.model.parser.template.trans.IAimlPerson2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * person2
 */
public class AimlPerson2Impl extends AimlFormatAltContainer implements
		IAimlPerson2 {

	protected Logger logger = LoggerFactory.getLogger(AimlPerson2Impl.class);

	/**
	 * constructor
	 */
	public AimlPerson2Impl() {
		super("person2");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlPerson2 [elements=" + elements + "]";
	}
}
