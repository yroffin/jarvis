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
 * The system element instructs the AIML interpreter to pass its content (with
 * any appropriate preprocessing, as noted above) to the system command
 * interpreter of the local machine on which the AIML interpreter is running.
 * The system element does not have any attributes.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:system>
 * 
 * <!-- Contents: character data, aiml-template-elements -->
 * 
 * </aiml:system>
 * 
 * The system element may return a value.
 */
public interface IAimlSystem extends IAimlElement {

}
