package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlPerson2;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlPerson2Impl extends AimlElementContainer implements IAimlPerson2 {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlPerson2 [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<person2/>");
		} else {
			render.append("<person2>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</person2>");
		}
		return render;
	}
}
