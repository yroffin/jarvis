package org.jarvis.main.model.impl.parser.template;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.AimlResult;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * format container
 */
public abstract class AimlFormatAltContainer extends AimlElementContainer {

	protected Logger logger = LoggerFactory.getLogger(AimlFormatAltContainer.class);

	/**
	 * @param tag
	 */
	public AimlFormatAltContainer(String tag) {
		super(tag);
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) {
		if (elements.size() > 0) {
			IAimlResult sb = null;
			try {
				sb = super.answer(engine, star, that, new AimlResult());
			} catch (AimlParsingError e) {
				logger.warn("Error {}", e);
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
}
