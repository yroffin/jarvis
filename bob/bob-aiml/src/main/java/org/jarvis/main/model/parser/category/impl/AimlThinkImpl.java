package org.jarvis.main.model.parser.category.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlThinkImpl extends AimlElementContainer implements IAimlThink {
	protected Logger	logger	= LoggerFactory.getLogger(AimlThinkImpl.class);

	public AimlThinkImpl() {
		super("think");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) throws AimlParsingError {
		/**
		 * The think element instructs the AIML interpreter to perform all usual
		 * processing of its contents, but to not return any value, regardless
		 * of whether the contents produce output.
		 */
		String local = super.answer(engine, star, that, new StringBuilder())
				.toString();
		logger.info("Think: " + local);
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThink [elements=" + elements + "]";
	}
}
