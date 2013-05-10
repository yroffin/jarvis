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
package org.jarvis.main.model.parser.template;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The gossip element instructs the AIML interpreter to capture the result of
 * processing the contents of the gossip elements and to store these contents in
 * a manner left up to the implementation. Most common uses of gossip have been
 * to store captured contents in a separate file.
 * 
 * The gossip element does not have any attributes. It may contain any AIML
 * template elements.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:gossip>
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:gossip>
 */
public interface IAimlGossip extends IAimlElement {
}
