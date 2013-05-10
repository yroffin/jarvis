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
import org.jarvis.main.model.parser.IAimlRepository;
import org.jarvis.main.model.parser.IAimlTopic;
import org.jarvis.main.model.parser.IAimlXml;

public class AimlRepository extends AimlElementContainer implements IAimlRepository {

	/**
	 * An AIML object is represented by an aiml:aiml element in an XML document.
	 * The aiml:aiml element may contain the following types of elements: -
	 * aiml:topic - aiml:category
	 */
	public AimlRepository() {
		super("aiml");
	}

	private List<IAimlTopic> topics = new ArrayList<IAimlTopic>();
	private List<IAimlCategory> categories = new ArrayList<IAimlCategory>();
	private IAimlXml root;

	@Override
	public void setRoot(IAimlXml root) {
		this.root = root;
	}

	@Override
	public void addCategory(IAimlCategory e) {
		categories.add(e);
	}

	@Override
	public void addTopic(IAimlTopic e) {
		topics.add(e);
	}

	@Override
	public StringBuilder toAiml(StringBuilder render) {
		root.toAiml(render);
		render.append("<aiml version=\"1.0\">\n");
		for(IAimlTopic e : topics) {
			e.toAiml(render);
		}
		for(IAimlCategory e : categories) {
			e.toAiml(render);
		}
		render.append("</aiml>\n");
		return render;
	}

	@Override
	public String toString() {
		return "AimlRepository [topics=" + topics + ", categories="
				+ categories + "]";
	}

	@Override
	public IAimlXml getRoot() {
		return root;
	}
}
