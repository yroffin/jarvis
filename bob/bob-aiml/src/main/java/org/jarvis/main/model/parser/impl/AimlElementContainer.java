package org.jarvis.main.model.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlElement;

public abstract class AimlElementContainer implements IAimlElement {
	protected List<IAimlElement> elements = new ArrayList<IAimlElement>();

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public void add(IAimlElement value) {
		elements.add(value);
	}

	@Override
	public void add(AimlProperty value) {
	}

}
