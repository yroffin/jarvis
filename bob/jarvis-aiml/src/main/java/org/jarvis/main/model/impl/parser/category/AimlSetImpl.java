package org.jarvis.main.model.impl.parser.category;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.impl.parser.AimlResult;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.category.IAimlSet;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class AimlSetImpl extends AimlElementContainer implements IAimlSet {

	public AimlSetImpl() {
		super("set");
	}

	private String name;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("name") == 0)
			name = accept(value);
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) throws AimlParsingError {
		IAimlResult set = super.answer(engine, star, that, new AimlResult());
		engine.set(name, set.getSpeech());
		render.append(set.getSpeech());
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSet [elements=" + elements + ", name=" + name
				+ "]";
	}
}
