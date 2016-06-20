package org.jarvis.main.model.impl.parser.template.system;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.AimlResult;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.system.IAimlJavascript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Javascript interpreter
 */
public class AimlJavascriptImpl extends AimlElementContainer implements
		IAimlJavascript {

	protected Logger logger = LoggerFactory.getLogger(AimlJavascriptImpl.class);

	/**
	 * constructor
	 */
	public AimlJavascriptImpl() {
		super("javascript");
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		/**
		 * The javascript element instructs the AIML interpreter to pass its
		 * content (with any appropriate preprocessing, as noted above) to a
		 * server-side JavaScript interpreter on the local machine on which the
		 * AIML interpreter is running. The javascript element does not have any
		 * attributes.
		 */
		IAimlResult local = super.answer(engine, star, that, new AimlResult());
		render.addJavascript(local.getSpeech());
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlJavascript [elements=" + elements + "]";
	}
}
