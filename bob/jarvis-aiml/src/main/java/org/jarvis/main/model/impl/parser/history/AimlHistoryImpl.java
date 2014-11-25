/**
 *  Copyright 2012 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.jarvis.main.model.impl.parser.history;

import java.util.List;

import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.transform.ITransformedItem;

public class AimlHistoryImpl implements IAimlHistory {

	private String input;
	private String answer;
	private ITransformedItem transformedAnswer;
	private ITransformedItem transformedPattern;
	private String javascript;
	private String script;
	private String think;

	public AimlHistoryImpl() {
	}

	public AimlHistoryImpl(String input, IAimlResult reply,
			IAimlTransform transformer) throws AimlParsingError {
		setInput(input);
		setAnswer(reply.getCleanSpeech());
		setThink(reply.getThink());
		setScript(reply.getScript());
		setJavascript(reply.getJavascript());
		/**
		 * fix transformation
		 */
		List<ITransformedItem> answers = transformer.transform(reply
				.getCleanSpeech());

		if (answers.size() > 0) {
			setTransformedAnswer(answers.get(answers.size() - 1));
		}
	}

	@Override
	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	@Override
	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public void setThink(String think) {
		this.think = think;
	}

	@Override
	public void setInput(String input) {
		this.input = input;
	}

	@Override
	public String getInput() {
		return input;
	}

	@Override
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String getAnswer() {
		return answer;
	}

	@Override
	public String getThink() {
		return think;
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public String getJavascript() {
		return javascript;
	}

	@Override
	public ITransformedItem getTransformedAnswer() {
		return transformedAnswer;
	}

	@Override
	public void setTransformedAnswer(ITransformedItem answer) {
		this.transformedAnswer = answer;
	}

	@Override
	public void setTransformedPattern(ITransformedItem pattern) {
		this.transformedPattern = pattern;

	}

	@Override
	public ITransformedItem getTransformedPattern() {
		return transformedPattern;
	}

	@Override
	public String toString() {
		return "AimlHistoryImpl [input=" + input + ", answer=" + answer
				+ ", transformedAnswer=" + transformedAnswer
				+ ", transformedPattern=" + transformedPattern
				+ ", javascript=" + javascript + ", script=" + script
				+ ", think=" + think + "]";
	}
}
