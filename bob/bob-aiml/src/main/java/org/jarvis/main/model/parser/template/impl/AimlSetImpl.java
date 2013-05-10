package org.jarvis.main.model.parser.template.impl;

import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlSet;

public class AimlSetImpl extends AimlElementContainer implements IAimlSet {

	public AimlSetImpl() {
		super("set");
	}

	private String name;

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public void add(IAimlProperty value) {
		if(value.getKey().compareTo("name")==0) name = accept(value);		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSet [elements=" + elements + ", name=" + name + "]";
	}
}
