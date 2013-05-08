package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlSet;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlSetImpl extends AimlElementContainer implements IAimlSet {

	private String name;

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("name")==0) name = value.getValue();		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSet [elements=" + elements + ", name=" + name + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<set name=\"" + name + "\"/>");
		} else {
			render.append("<set name=\"" + name + "\">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</set>");
		}
		return render;
	}
}
