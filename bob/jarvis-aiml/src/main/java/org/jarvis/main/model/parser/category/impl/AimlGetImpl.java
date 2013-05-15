package org.jarvis.main.model.parser.category.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlGet;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlGetImpl extends AimlElementContainer implements IAimlGet {

	public AimlGetImpl() {
		super("get");
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
		if (name == null) {
			render.append("unknown");
		} else {
			render.append(engine.get(name));
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlGet [elements=" + elements + ", name=" + name
				+ "]";
	}
}
