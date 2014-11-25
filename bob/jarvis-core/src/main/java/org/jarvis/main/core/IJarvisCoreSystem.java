package org.jarvis.main.core;

import java.io.IOException;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;

public interface IJarvisCoreSystem {

	public void release();

	public void initialize(String botname, String aiml)
			throws AimlParsingError, IOException;

	public List<IAimlHistory> ask(String string) throws AimlParsingError;

	public List<IAimlHistory> chat(String sentence) throws AimlParsingError;

	public void speak(String value) throws IOException;

	IAimlCoreEngine getEngine();
}
