package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlId;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlIdImpl extends AimlElementContainer implements IAimlId {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlId [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<id/>");
		} else {
			render.append("<id>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</id>");
		}
		return render;
	}
}
