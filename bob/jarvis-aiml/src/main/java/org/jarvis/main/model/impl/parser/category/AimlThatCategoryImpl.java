package org.jarvis.main.model.impl.parser.category;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.category.IAimlThat;

public class AimlThatCategoryImpl extends AimlElementContainer implements IAimlThat {

	private IAimlCategory category;

	public AimlThatCategoryImpl(IAimlCategory category) {
		super("that");
		this.category = category;
	}

	@Override
	public IAimlTopic getTopic() {
		return category.getTopic();
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlCategorySideThat [elements=" + elements + "]";
	}
}
