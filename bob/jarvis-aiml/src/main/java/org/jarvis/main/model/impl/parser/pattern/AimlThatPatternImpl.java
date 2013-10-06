package org.jarvis.main.model.impl.parser.pattern;

import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.category.IAimlPattern;
import org.jarvis.main.model.parser.category.IAimlThat;

public class AimlThatPatternImpl extends AimlElementContainer implements IAimlThat {

	private IAimlPattern pattern;

	public AimlThatPatternImpl(IAimlPattern e) {
		super("that");
		pattern = e;
	}

	@Override
	public IAimlTopic getTopic() {
		return pattern.getTopic();
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
