package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlFormal;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlFormalImpl extends AimlElementContainer implements IAimlFormal {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<formal/>");
		} else {
			render.append("<formal>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</formal>");
		}
		return render;
	}
}
