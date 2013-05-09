package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlInputImpl extends AimlElementContainer implements IAimlInput {

	public AimlInputImpl() {
		super("input");
	}

	private String index;

	@Override
	public void setIndex(String index) {
		this.index = index;		
	}

	@Override
	public void add(IAimlProperty value) {
		if(value.getKey().compareTo("index")==0) index = accept(value);		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlInput [elements=" + elements + ", index=" + index + "]";
	}
}
