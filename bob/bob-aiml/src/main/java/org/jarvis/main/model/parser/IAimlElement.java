package org.jarvis.main.model.parser;

import org.jarvis.main.model.parser.impl.AimlProperty;

public interface IAimlElement {

	public void add(String value);
	public void add(IAimlElement value);
	public void add(AimlProperty value);
	public StringBuilder toAiml(StringBuilder render);

}
