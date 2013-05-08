package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlGet;

public class AimlGet extends AimlElementContainer implements IAimlGet {

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
		return "\n\t\t\t\tAimlGet [elements=" + elements + ", name=" + name + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<get name=\"" + name + "\"/>");
		} else {
			render.append("<get name=\"" + name + "\">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</get>");
		}
		return render;
	}
}
