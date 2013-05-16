package org.jarvis.main.model.impl.parser.template.trans;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.trans.IAimlPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlPersonImpl extends AimlElementContainer implements IAimlPerson {

	protected Logger logger = LoggerFactory.getLogger(AimlPersonImpl.class);

	public AimlPersonImpl() {
		super("person");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) {
		if (elements.size() > 0) {
			StringBuilder sb = null;
			try {
				sb = super.answer(engine, star, that, new StringBuilder());
			} catch (AimlParsingError e) {
				e.printStackTrace();
				logger.warn(e.getMessage());
			}
			if (sb != null) {
				render.append(sb.substring(0, 1).toUpperCase()
						+ sb.substring(1).toLowerCase());
			}
		} else {
			if (star.size() > 0) {
				for (String value : star) {
					render.append(value.substring(0, 1).toUpperCase()
							+ value.substring(1).toLowerCase());
				}
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
