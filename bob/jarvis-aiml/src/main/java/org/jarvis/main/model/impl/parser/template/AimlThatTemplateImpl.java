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
package org.jarvis.main.model.impl.parser.template;

import java.util.List;
import java.util.Stack;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.history.IAimlHistory;

public class AimlThatTemplateImpl extends AimlElementContainer implements
		IAimlThat {

	public AimlThatTemplateImpl() {
		super("that");
	}

	private String index;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("index") == 0) index = accept(value);
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) throws AimlParsingError {
		Stack<List<IAimlHistory>> inputs = engine.getHistory();
		if (index == null) {
			if (inputs.size() > 0 && inputs.get(0).size() > 0) {
				render.append(inputs.get(0).get(0).getAnswer());
			}
		} else {
			int ix = 1, iy = 1;
			String[] indexes = index.split(",");
			if (indexes.length == 1) {
				ix = Integer.parseInt(indexes[0]);
				iy = 1;
			}
			if (indexes.length == 2) {
				ix = Integer.parseInt(indexes[0]);
				iy = Integer.parseInt(indexes[1]);
			}
			if (inputs.size() >= ix && inputs.get(ix - 1).size() >= iy) {
				render.append(inputs.get(ix - 1).get(iy - 1).getAnswer());
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlTemplateSideThat [elements=" + elements + "]";
	}
}
