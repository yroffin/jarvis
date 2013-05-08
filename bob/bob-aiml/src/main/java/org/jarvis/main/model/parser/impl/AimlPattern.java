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

package org.jarvis.main.model.parser.impl;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlPattern extends AimlElementContainer implements IAimlPattern {
	protected Logger logger = LoggerFactory.getLogger(AimlPattern.class);

	@Override
	public String toString() {
		return "\n\t\t\tAimlPattern [elements=" + elements + "]";
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		render.append("<pattern>");
		for(IAimlElement e : elements) {
			e.toAiml(render);
		}
		render.append("</pattern>");
		return render;
	}
}
