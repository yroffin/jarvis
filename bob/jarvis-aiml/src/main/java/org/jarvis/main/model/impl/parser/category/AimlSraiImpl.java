package org.jarvis.main.model.impl.parser.category;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlSrai;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class AimlSraiImpl extends AimlElementContainer implements IAimlSrai {

	public AimlSraiImpl() {
		super("srai");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError {
		StringBuilder sb = new StringBuilder();
		for (IAimlElement element : elements) {
			element.answer(engine, star, that, sb);
		}
		List<IAimlHistory> list = engine.ask(sb.toString());
		for (IAimlHistory value : list) {
			render.append(value.getAnswer());
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSrai [elements=" + elements + "]";
	}
}
