package org.jarvis.main.core;

import java.io.IOException;

import org.jarvis.main.exception.AimlParsingError;

public interface IJarvisCoreSystem {

	void release();

	void speak(String value);

	void initialize(String aiml) throws AimlParsingError, IOException;

	void ask(String string) throws AimlParsingError;

}
