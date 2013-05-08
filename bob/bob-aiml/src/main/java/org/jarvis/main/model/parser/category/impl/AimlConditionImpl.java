package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlCondition;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlConditionImpl extends AimlElementContainer implements IAimlCondition {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCondition [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<condition/>");
		} else {
			render.append("<condition>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</condition>");
		}
		return render;
	}
}
