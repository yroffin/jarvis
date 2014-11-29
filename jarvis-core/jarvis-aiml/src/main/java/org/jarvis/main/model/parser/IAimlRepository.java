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

import java.util.List;

/**
 * An AIML object is represented by an aiml:aiml element in an XML document. The
 * aiml:aiml element may contain the following types of elements: - aiml:topic -
 * aiml:category
 */
public interface IAimlRepository extends IAimlElement {
	List<IAimlTopic> getTopics();

	List<IAimlCategory> getCategories();
	List<IAimlCategory> getCategoriesFilteredByTopic(String filter);

	void addCategory(IAimlCategory currentCategory);

	void addTopic(IAimlTopic currentTopic);

	void setRoot(IAimlXml root);

	IAimlXml getRoot();

	String getStatistics();
}
