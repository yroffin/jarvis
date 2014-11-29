package org.jarvis.main.model.parser;

public interface IAimlResult {

	String getSpeech();

	String substring(int i, int j);

	String substring(int i);

	void append(String string);

	String getCleanSpeech();

	String addThink(String value);

	String addScript(String string);

	String addJavascript(String string);

	String getThink();

	String getScript();

	String getJavascript();

}
