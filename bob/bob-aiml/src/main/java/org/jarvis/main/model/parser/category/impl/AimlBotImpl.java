package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlBot;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlBotImpl extends AimlElementContainer implements IAimlBot {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("name")==0) name = value.getValue();		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlBot [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<bot name=\"" + name + "\"/>");
		} else {
			render.append("<bot name=\"" + name + "\">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</bot>");
		}
		return render;
	}
}
