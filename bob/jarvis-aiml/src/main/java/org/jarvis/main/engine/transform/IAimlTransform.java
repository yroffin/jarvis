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

package org.jarvis.main.engine.transform;

import java.util.List;

import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.transform.ITransformedItem;

public interface IAimlTransform {
	public List<ITransformedItem> transform(String data)
			throws AimlParsingError;

	public List<ITransformedItem> transform(List<IAimlElement> elements)
			throws AimlParsingError;

	public boolean compare(String left, String right) throws AimlParsingError;
}
