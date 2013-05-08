package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlSrai;

public class AimlSrai extends AimlElementContainer implements IAimlSrai {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSrai [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<srai/>");
		} else {
			render.append("<srai>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</srai>");
		}
		return render;
	}
}
