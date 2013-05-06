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

import java.util.ArrayList;
import java.util.List;

import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.IAimlPattern;
import org.jarvis.main.model.parser.IAimlTemplate;
import org.jarvis.main.model.parser.IAimlCategoryElement;

public class AimlCategory implements IAimlCategory {

	private IAimlTemplate template;
	private IAimlPattern pattern;
	
	private List<IAimlCategoryElement> elements = new ArrayList<IAimlCategoryElement>();
	
	@Override
	public void setTemplate(IAimlTemplate e) {
		template = e;		
	}

	@Override
	public void setPattern(IAimlPattern e) {
		pattern = e;
	}

	@Override
	public void add(String value) {
		elements.add(new AimlData(value));
	}

	@Override
	public void add(IAimlCategoryElement e) {
		elements.add(e);		
	}

	@Override
	public String toString() {
		return "\n\t\tAimlCategory [template=" + template + ", pattern=" + pattern
				+ ", elements=" + elements + "]";
	}
}
