package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlPerson;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlPersonImpl extends AimlElementContainer implements IAimlPerson {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<person/>");
		} else {
			render.append("<person>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</person>");
		}
		return render;
	}
}
