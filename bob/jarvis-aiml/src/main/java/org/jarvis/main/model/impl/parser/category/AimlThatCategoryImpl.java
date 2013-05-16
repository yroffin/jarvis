package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.category.IAimlThat;

public class AimlThatCategoryImpl extends AimlElementContainer implements IAimlThat {

	public AimlThatCategoryImpl() {
		super("that");
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCategorySideThat [elements=" + elements + "]";
	}
}
