package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlBr;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlBrImpl extends AimlElementContainer implements IAimlBr {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlBr [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<Br/>");
		} else {
			render.append("<Br>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</Br>");
		}
		return render;
	}
}
