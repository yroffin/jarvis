package org.jarvis.main.model.impl.parser.category;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.AimlResult;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlThinkImpl extends AimlElementContainer implements IAimlThink {
	protected Logger logger = LoggerFactory.getLogger(AimlThinkImpl.class);

	public AimlThinkImpl() {
		super("think");
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		/**
		 * The think element instructs the AIML interpreter to perform all usual
		 * processing of its contents, but to not return any value, regardless
		 * of whether the contents produce output.
		 */
		IAimlResult thought = super
				.answer(engine, star, that, new AimlResult());
		render.addThink(thought.getSpeech());
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThink [elements=" + elements + "]";
	}
}
