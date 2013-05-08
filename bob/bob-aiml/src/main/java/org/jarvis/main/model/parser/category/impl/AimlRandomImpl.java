package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlRandom;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlRandomImpl extends AimlElementContainer implements IAimlRandom {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<random/>");
		} else {
			render.append("<random>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</random>");
		}
		return render;
	}
}
