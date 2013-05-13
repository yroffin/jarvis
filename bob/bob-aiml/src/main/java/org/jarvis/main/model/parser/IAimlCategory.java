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

package org.jarvis.main.model.parser;

import org.jarvis.main.model.parser.category.IAimlPattern;
import org.jarvis.main.model.parser.category.IAimlTemplate;
import org.jarvis.main.model.parser.category.IAimlThat;
import org.jarvis.main.model.transform.ITransformedItem;

public interface IAimlCategory extends IAimlElement {
	public void setPattern(IAimlPattern e);

	public IAimlPattern getPattern();

	public void setTemplate(IAimlTemplate e);

	public void setTransformedPattern(ITransformedItem transform);

	public void setThat(IAimlThat e);

	public IAimlThat getThat();

	public ITransformedItem getTransformedPattern();

	public boolean hasThat();

	public void setTransformedThat(ITransformedItem transform);

	public ITransformedItem getTransformedThat();
}
