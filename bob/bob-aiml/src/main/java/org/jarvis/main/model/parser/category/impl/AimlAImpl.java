package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlA;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlAImpl extends AimlElementContainer implements IAimlA {

	public AimlAImpl() {
		super("a");
	}

	private String href;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	@Override
	public void add(IAimlProperty value) {
		if(value.getKey().compareTo("href")==0) href = accept(value);		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlA [elements=" + elements + "]";
	}
}
