package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlGet;
import org.jarvis.main.model.parser.IAimlGetElement;

public class AimlGet implements IAimlGet {

	private List<IAimlGetElement> elements = new ArrayList<IAimlGetElement>();
	private String name;

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlGet [elements=" + elements + ", name=" + name + "]";
	}

}
