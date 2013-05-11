package org.jarvis.main.model.parser;

import java.util.List;

public interface IAimlElement extends IAimlRender {
	public void add(String value);

	public void add(IAimlElement value);

	public void add(IAimlProperty value);

	public String get(String key);

	@Override
	public StringBuilder toAiml(StringBuilder render);

	List<IAimlElement> getElements();

}
