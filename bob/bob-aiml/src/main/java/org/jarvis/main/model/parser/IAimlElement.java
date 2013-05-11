package org.jarvis.main.model.parser;

import java.util.List;

public interface IAimlElement extends IAimlRender {
	public void add(String value);

	public void add(IAimlElement value);

	public void add(IAimlProperty value);

	public String get(String key);

	public List<IAimlElement> getElements();

	/**
	 * compute an answer
	 * 
	 * @param star
	 * @param that
	 * @param render
	 */
	public void answer(String star, String that, StringBuilder render);

	@Override
	public StringBuilder toAiml(StringBuilder render);
}
