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

package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;
import org.jarvis.main.model.parser.IAimlTopic;

/**
 * A template is an element that appears within category elements. The template must follow the pattern-side that element, if it exists; otherwise, it follows the pattern element.
 * A template does not have any attributes.
 * 
 * An atomic template element in AIML indicates to an AIML interpreter that it must return a value according to the functional meaning of the element. Atomic elements do not have any content.
 */
public interface IAimlTemplate extends IAimlElement {

	IAimlTopic getTopic();
}
