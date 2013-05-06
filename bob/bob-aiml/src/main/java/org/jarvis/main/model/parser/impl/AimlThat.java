package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlThat;
import org.jarvis.main.model.parser.IAimlThatElement;

public class AimlThat implements IAimlThat {

	private List<IAimlThatElement> elements = new ArrayList<IAimlThatElement>();

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThat [elements=" + elements + "]";
	}

}
