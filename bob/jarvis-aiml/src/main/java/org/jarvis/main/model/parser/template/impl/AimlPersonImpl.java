package org.jarvis.main.model.parser.template.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlPerson;

public class AimlPersonImpl extends AimlElementContainer implements IAimlPerson {

	public AimlPersonImpl() {
		super("person");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) {
		if (star.size() > 0) {
			for (String value : star) {
				render.append(value.substring(0, 1).toUpperCase()
						+ value.substring(1));
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}
}
