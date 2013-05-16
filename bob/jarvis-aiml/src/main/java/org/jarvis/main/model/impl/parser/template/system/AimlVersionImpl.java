package org.jarvis.main.model.impl.parser.template.system;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlVersion;

public class AimlVersionImpl extends AimlElementContainer implements IAimlVersion {

	public AimlVersionImpl() {
		super("version");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlVersion [elements=" + elements + "]";
	}
}
