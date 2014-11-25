package org.jarvis.main.model.impl.parser.template.format;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.AimlResult;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.format.IAimlLowercase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlLowercaseImpl extends AimlElementContainer implements
		IAimlLowercase {

	protected Logger logger = LoggerFactory.getLogger(AimlLowercaseImpl.class);

	public AimlLowercaseImpl() {
		super("lowercase");
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) {
		if (elements.size() > 0) {
			IAimlResult sb = null;
			try {
				sb = super.answer(engine, star, that, new AimlResult());
			} catch (AimlParsingError e) {
				e.printStackTrace();
				logger.warn(e.getMessage());
			}
			if (sb != null) {
				render.append(sb.substring(0).toLowerCase());
			}
		} else {
			if (star.size() > 0) {
				for (String value : star) {
					render.append(value.substring(0).toLowerCase());
				}
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlLowercase [elements=" + elements + "]";
	}
}
