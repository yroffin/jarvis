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
package org.jarvis.main.model.parser.template.system;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The javascript element instructs the AIML interpreter to pass its content
 * (with any appropriate preprocessing, as noted above) to a server-side
 * JavaScript interpreter on the local machine on which the AIML interpreter is
 * running. The javascript element does not have any attributes.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:javascript>
 * 
 * <!-- Contents: character data, aiml-template-elements -->
 * 
 * </aiml:javascript>
 * 
 * AIML 1.0.1 does not require that an AIML interpreter include a server-side
 * JavaScript interpreter, and does not require any particular behavior from the
 * server-side JavaScript interpreter if it exists.
 * 
 * The javascript element may return a value.
 */
public interface IAimlJavascript extends IAimlElement {

}
