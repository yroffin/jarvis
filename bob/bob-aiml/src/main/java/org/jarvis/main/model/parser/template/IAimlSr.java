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
 * Several atomic AIML elements are "short-cuts" for combinations of other AIML
 * elements. They are listed here without further explanation; the reader should
 * refer to the descriptions of the "long form" of each element for which the
 * following elements are short-cuts. The sr element is a shortcut for:
 * 
 * <srai><star/></srai>
 * 
 * The atomic sr does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:sr/>
 */
public interface IAimlSr extends IAimlElement {
}
