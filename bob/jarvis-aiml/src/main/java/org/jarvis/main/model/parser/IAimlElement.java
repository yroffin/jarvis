package org.jarvis.main.model.parser;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;

public interface IAimlElement extends IAimlRender {
	public void add(String value);

	public void add(IAimlElement value);

	public void add(IAimlProperty value);

	public String get(String key);

	public List<IAimlElement> getElements();

	/**
	 * compute an answer
	 * 
	 * @param engine
	 * @param star
	 * @param that
	 * @param render
	 * @throws AimlParsingError
	 */
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError;

	@Override
	public StringBuilder toAiml(StringBuilder render);
}
