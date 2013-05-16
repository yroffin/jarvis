package org.jarvis.main.model.impl.parser.template.system;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.template.system.IAimlJavascript;

public class AimlJavascriptImpl extends AimlElementContainer implements
		IAimlJavascript {

	public AimlJavascriptImpl() {
		super("javascript");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlJavascript [elements=" + elements + "]";
	}
}
