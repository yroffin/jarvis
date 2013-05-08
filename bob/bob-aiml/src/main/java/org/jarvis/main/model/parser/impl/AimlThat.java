package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlThat;

public class AimlThat extends AimlElementContainer implements IAimlThat {

	private String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("index")==0) index = value.getValue();		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThat [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			if(index != null) {
				render.append("<that index=\"" + index + "\"/>");
			} else {
				render.append("<that/>");
			}
		} else {
			if(index != null) {
				render.append("<that index=\"" + index + "\">");
			} else {
				render.append("<that>");
			}
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</that>");
		}
		return render;
	}
}
