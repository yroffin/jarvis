package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlSrai;
import org.jarvis.main.model.parser.IAimlSraiElement;

public class AimlSrai implements IAimlSrai {

	private List<IAimlSraiElement> elements = new ArrayList<IAimlSraiElement>();

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlSrai [elements=" + elements + "]";
	}

}
