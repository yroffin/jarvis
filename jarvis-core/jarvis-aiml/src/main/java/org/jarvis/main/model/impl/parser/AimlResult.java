package org.jarvis.main.model.impl.parser;

import org.jarvis.main.model.parser.IAimlResult;

public class AimlResult implements IAimlResult {

	private StringBuilder speech = new StringBuilder();
	private StringBuilder think = new StringBuilder();
	private StringBuilder script = new StringBuilder();
	private StringBuilder javascript = new StringBuilder();

	@Override
	public String getSpeech() {
		return speech.toString();
	}

	@Override
	public String addThink(String think) {
		this.think.append(think);
		return think;
	}

	@Override
	public String addScript(String script) {
		this.script.append(script);
		return script;
	}

	@Override
	public String addJavascript(String javascript) {
		this.javascript.append(javascript);
		return javascript;
	}

	@Override
	public String getThink() {
		return think.toString();
	}

	@Override
	public String getScript() {
		return script.toString();
	}

	@Override
	public String getJavascript() {
		return javascript.toString();
	}

	@Override
	public String substring(int i, int j) {
		return speech.substring(i, j);
	}

	@Override
	public String substring(int i) {
		return speech.substring(i);
	}

	@Override
	public void append(String str) {
		speech.append(str);
	}

	@Override
	public String getCleanSpeech() {
		return speech.toString().replace("\n", "").replace("\r", "").trim();
	}

	@Override
	public String toString() {
		return "AimlResult [speech=" + speech + ", think=" + think
				+ ", script=" + script + ", javascript=" + javascript + "]";
	}

}
