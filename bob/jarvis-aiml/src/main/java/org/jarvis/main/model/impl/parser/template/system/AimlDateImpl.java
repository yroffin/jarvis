package org.jarvis.main.model.impl.parser.template.system;

import java.util.Date;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.system.IAimlDate;

public class AimlDateImpl extends AimlElementContainer implements IAimlDate {

	public AimlDateImpl() {
		super("date");
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) {
		render.append((new Date()) + "");
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlDate [elements=" + elements + "]";
	}
}
