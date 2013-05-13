package org.jarvis.main.model.parser.category.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlSet;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlSetImpl extends AimlElementContainer implements IAimlSet {

	public AimlSetImpl() {
		super("set");
	}

	private String	name;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("name") == 0) name = accept(value);
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) throws AimlParsingError {
		String set = super.answer(engine, star, that, new StringBuilder())
				.toString();
		engine.set(name, set);
		render.append(set);
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSet [elements=" + elements + ", name=" + name
				+ "]";
	}
}
