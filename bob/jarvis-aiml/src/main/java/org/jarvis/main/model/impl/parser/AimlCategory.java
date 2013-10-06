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

package org.jarvis.main.model.impl.parser;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.category.IAimlPattern;
import org.jarvis.main.model.parser.category.IAimlTemplate;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class AimlCategory extends AimlElementContainer implements IAimlCategory {

	/**
	 * A category is a top-level (or second-level, if contained within a topic)
	 * element that contains exactly one pattern and exactly one template. A
	 * category does not have any attributes.
	 */
	public AimlCategory(IAimlTopic topic) {
		super("category");
		this.topic = topic;
	}

	private IAimlTemplate template;
	private IAimlPattern pattern;
	private IAimlThat that;
	private IAimlHistory history;
	private IAimlTopic topic;

	@Override
	public IAimlPattern getPattern() {
		return pattern;
	}

	@Override
	public void setPattern(IAimlPattern e) {
		pattern = e;
	}

	@Override
	public void setTemplate(IAimlTemplate e) {
		template = e;
	}

	@Override
	public void setHistory(IAimlHistory e) {
		history = e;
	}

	@Override
	public IAimlHistory getHistory() {
		return history;
	}

	@Override
	public IAimlThat getThat() {
		return that;
	}

	@Override
	public void setThat(IAimlThat e) {
		this.that = e;
	}

	@Override
	public boolean hasThat() {
		return that != null;
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError {
		for (IAimlElement element : template.getElements()) {
			/**
			 * TODO here that must be local that
			 */
			element.answer(engine, star, that, render);
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\tAimlCategory [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		if (elements.size() == 0) {
			render.append("<" + tag);
			properties(render);
			render.append("/>\n");
		} else {
			render.append("<" + tag);
			properties(render);
			render.append(">");
			for (IAimlElement e : elements) {
				e.toAiml(render);
			}
			render.append("</" + tag + ">\n");
		}
		return render;
	}

	@Override
	public IAimlTopic getTopic() {
		return topic;
	}
}
