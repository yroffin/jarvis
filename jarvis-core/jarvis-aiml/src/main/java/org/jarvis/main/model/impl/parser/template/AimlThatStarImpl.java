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

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.IAimlProperty;
import org.jarvis.main.model.parser.IAimlResult;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.IAimlThatStar;
import org.jarvis.main.model.transform.ITransformedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * that context
 *
 */
public class AimlThatStarImpl extends AimlElementContainer implements
		IAimlThatStar {

	protected Logger logger = LoggerFactory.getLogger(AimlThatStarImpl.class);
	private final IAimlThat thatElement;

	/**
	 * @param that
	 */
	public AimlThatStarImpl(IAimlThat that) {
		super("thatstar");
		this.thatElement = that;
	}

	private int index;

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public void add(IAimlProperty value) {
		if (value.getKey().compareTo("index") == 0)
			index = Integer.parseInt(accept(value));
	}

	@Override
	public IAimlResult answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, IAimlResult render) {
		List<ITransformedItem> list = null;
		try {
			list = thatElement.getTransforms(thatElement.getTopic());
			List<String> local = that.getTransformedAnswer().star(list.get(0),
					new ArrayList<String>());
			if (local.size() > 0 && index < local.size()) {
				render.append(local.get(index));
			}
		} catch (AimlParsingError e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlThatStar [elements=" + elements + "]";
	}
}
