package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlVersion;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlVersionImpl extends AimlElementContainer implements IAimlVersion {

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlVersion [elements=" + elements + "]";
	}

	@Override
	public void add(AimlProperty value) {
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<version/>");
		} else {
			render.append("<version>");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</version>");
		}
		return render;
	}
}
