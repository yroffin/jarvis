package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlInput;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlInputImpl extends AimlElementContainer implements IAimlInput {

	private String index;

	@Override
	public void setIndex(String index) {
		this.index = index;		
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("index")==0) index = value.getValue();		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlInput [elements=" + elements + ", index=" + index + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<input index=\"" + index + "\"/>");
		} else {
			render.append("<input index=\"" + index + "\">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</input>");
		}
		return render;
	}
}
