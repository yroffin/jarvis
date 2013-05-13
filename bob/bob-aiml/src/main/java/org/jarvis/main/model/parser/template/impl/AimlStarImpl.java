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
package org.jarvis.main.model.parser.template.impl;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.impl.AimlElementContainer;
import org.jarvis.main.model.parser.template.IAimlStar;

public class AimlStarImpl extends AimlElementContainer implements IAimlStar {

	public AimlStarImpl() {
		super("star");
	}

	private int	index	= 0;

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("index") == 0) index = Integer
				.parseInt(accept(value));
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			String that, StringBuilder render) {
		if (star.size() > 0 && index < star.size()) {
			render.append(star.get(index));
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlStar [elements=" + elements + "]";
	}
}
