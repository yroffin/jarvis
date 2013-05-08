package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.category.IAimlA;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.impl.AimlProperty;

public class AimlAImpl extends AimlElementContainer implements IAimlA {

	private String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void add(AimlProperty value) {
		if(value.getKey().compareTo("href")==0) href = value.getValue();		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlA [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if(elements.size() == 0) {
			render.append("<a href=\"" + href + "\"/>");
		} else {
			render.append("<a href=\"" + href + "\">");
			for(IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</a>");
		}
		return render;
	}
}
