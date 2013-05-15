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

/**
 * The get element tells the AIML interpreter that it should substitute the
 * contents of a predicate, if that predicate has a value defined. If the
 * predicate has no value defined, the AIML interpreter should substitute the
 * empty string "".
 * 
 * The AIML interpreter implementation may optionally provide a mechanism that
 * allows the AIML author to designate default values for certain predicates
 * (see [9.3.]).
 * 
 * The get element must not perform any text formatting or other "normalization"
 * on the predicate contents when returning them.
 * 
 * The get element has a required name attribute that identifies the predicate
 * with an AIML predicate name.
 * 
 * The get element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:get name = aiml-predicate-name />
 */
public interface IAimlGet extends IAimlElement {
	public void setName(String name);
}
