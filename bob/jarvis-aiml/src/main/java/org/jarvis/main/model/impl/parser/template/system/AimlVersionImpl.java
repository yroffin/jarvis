package org.jarvis.main.model.impl.parser.template.system;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.system.IAimlVersion;

public class AimlVersionImpl extends AimlElementContainer implements
		IAimlVersion {

	public AimlVersionImpl() {
		super("version");
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) {
		render.append("1.0.1");
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlVersion [elements=" + elements + "]";
	}
}
