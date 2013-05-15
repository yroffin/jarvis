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
 * The set element instructs the AIML interpreter to set the value of a
 * predicate to the result of processing the contents of the set element. The
 * set element has a required attribute name, which must be a valid AIML
 * predicate name. If the predicate has not yet been defined, the AIML
 * interpreter should define it in memory.
 * 
 * The AIML interpreter should, generically, return the result of processing the
 * contents of the set element. The set element must not perform any text
 * formatting or other "normalization" on the predicate contents when returning
 * them.
 * 
 * The AIML interpreter implementation may optionally provide a mechanism that
 * allows the AIML author to designate certain predicates as
 * "return-name-when-set", which means that a set operation using such a
 * predicate will return the name of the predicate, rather than its captured
 * value. (See [9.2].)
 * 
 * A set element may contain any AIML template elements.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:set name = aiml-predicate-name >
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:set>
 */
public interface IAimlSet extends IAimlElement {
	public void setName(String name);
}
