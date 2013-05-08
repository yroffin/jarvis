package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlThink;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlThinkImpl extends AimlElementContainer implements IAimlThink {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlRandom [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<think/>");
		} else {
			render.append("<think>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</think>");
		}
		return render;
	}
}
