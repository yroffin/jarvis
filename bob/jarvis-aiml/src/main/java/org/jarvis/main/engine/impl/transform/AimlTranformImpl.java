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

package org.jarvis.main.engine.impl.transform;

import java.util.List;

import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.engine.transform.IAimlTransformParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlDataImpl;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.transform.ITransformedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlTranformImpl implements IAimlTransform {
	protected Logger logger = LoggerFactory.getLogger(AimlTranformImpl.class);

	@Override
	public List<ITransformedItem> transform(IAimlTopic topic, List<IAimlElement> elements)
			throws AimlParsingError {
		StringBuilder render = new StringBuilder();
		StringBuilder transform = new StringBuilder();
		for (IAimlElement element : elements) {
			transform.setLength(0);
			if (element.getClass() == AimlDataImpl.class) {
				element.toAiml(transform);
				render.append(transform.toString());
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("[TRANSFORM] " + topic + " => " + transform(render.toString()));
		}
		return transform(render.toString());
	}

	@Override
	public List<ITransformedItem> transform(String data)
			throws AimlParsingError {
		IAimlTransformParser parser;
		if (data != null) {
			parser = new AimlTransformParserImpl(data);
		} else {
			/**
			 * null value handler
			 */
			parser = new AimlTransformParserImpl("");
		}
		return parser.parse();
	}

	@Override
	public boolean compare(String left, String right) throws AimlParsingError {
		List<ITransformedItem> transformedLeft = transform(left);
		List<ITransformedItem> transformedRight = transform(right);
		if (transformedLeft.size() != transformedRight.size()) return false;

		int i = 0;
		boolean scored = true;
		for (ITransformedItem tx : transformedLeft) {
			scored = scored && (tx.score(transformedRight.get(i++)) > 0);
		}
		return scored;
	}
}
