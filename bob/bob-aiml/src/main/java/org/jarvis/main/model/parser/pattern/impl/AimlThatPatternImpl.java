package org.jarvis.main.model.parser.pattern.impl;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlThatPatternImpl extends AimlElementContainer implements IAimlThat {

	public AimlThatPatternImpl() {
		super("that");
	}

	private String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public void add(IAimlProperty value) {
		if(value.getKey().compareTo("index")==0) index = accept(value);		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlPatternSideThat [elements=" + elements + "]";
	}
}
