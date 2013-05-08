package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlLi;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlLiImpl extends AimlElementContainer implements IAimlLi {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<li/>");
		} else {
			render.append("<li>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</li>");
		}
		return render;
	}
}
