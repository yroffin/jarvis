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
 * The atomic version of the person2 element is a shortcut for:
 * 
 * <person2><star/></person2>
 * 
 * The atomic person2 does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:person2/>
 */
public interface IAimlPerson2 extends IAimlElement {
}
