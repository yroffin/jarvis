package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlStar;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlStarImpl extends AimlElementContainer implements IAimlStar {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlStar [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<star/>");
		} else {
			render.append("<star>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</star>");
		}
		return render;
	}
}
