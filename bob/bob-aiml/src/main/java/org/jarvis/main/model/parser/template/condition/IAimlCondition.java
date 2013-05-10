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
package org.jarvis.main.model.parser.template.condition;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The condition element instructs the AIML interpreter to return specified
 * contents depending upon the results of matching a predicate against a
 * pattern.
 * 
 * NB: The condition element has three different types. The three different
 * types specified here are distinguished by an xsi:type attribute, which
 * permits a validating XML Schema processor to validate them. Two of the types
 * may contain li elements, of which there are three different types, whose
 * validity is determined by the type of enclosing condition. In practice, an
 * AIML interpreter may allow the omission of the xsi:type attribute and may
 * instead heuristically determine which type of condition (and hence li) is in
 * use.
 */
public interface IAimlCondition extends IAimlElement {

}
