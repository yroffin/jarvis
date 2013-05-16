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
package org.jarvis.main.model.impl.parser.template.trans;

import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlElementContainer;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.jarvis.main.model.parser.template.trans.IAimlGender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlGenderImpl extends AimlElementContainer implements IAimlGender {

	protected Logger logger = LoggerFactory.getLogger(AimlGenderImpl.class);

	public AimlGenderImpl() {
		super("gender");
	}

	@Override
	public StringBuilder answer(IAimlCoreEngine engine, List<String> star,
			IAimlHistory that, StringBuilder render) {
		if (elements.size() > 0) {
			StringBuilder sb = null;
			try {
				sb = super.answer(engine, star, that, new StringBuilder());
			} catch (AimlParsingError e) {
				e.printStackTrace();
				logger.warn(e.getMessage());
			}
			if (sb != null) {
				render.append(sb.substring(0).toLowerCase());
			}
		} else {
			if (star.size() > 0) {
				for (String value : star) {
					render.append(value.substring(0).toLowerCase());
				}
			}
		}
		return render;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tAimlGender [elements=" + elements + "]";
	}
}
