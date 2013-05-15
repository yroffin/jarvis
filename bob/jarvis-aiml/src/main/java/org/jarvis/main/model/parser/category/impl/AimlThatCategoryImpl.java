package org.jarvis.main.model.parser.category.impl;

import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.impl.AimlElementContainer;

public class AimlThatCategoryImpl extends AimlElementContainer implements IAimlThat {

	public AimlThatCategoryImpl() {
		super("that");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCategorySideThat [elements=" + elements + "]";
	}
}
