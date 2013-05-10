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
 * The atomic version of the person element is a shortcut for:
 * 
 * <person><star/></person>
 * 
 * The atomic person does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:person/>
 * 
 * The person element instructs the AIML interpreter to:
 * 
 * replace words with first-person aspect in the result of processing the
 * contents of the person element with words with the
 * grammatically-corresponding second-person aspect; and replace words with
 * second-person aspect in the result of processing the contents of the person
 * element with words with the grammatically-corresponding first-person aspect.
 * The definition of "grammatically-corresponding" is left up to the
 * implementation.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:person>
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:person>
 * 
 * Historically, implementations of person have dealt with pronouns, likely due
 * to the fact that most AIML has been written in English. However, the decision
 * about whether to transform the person aspect of other words is left up to the
 * implementation.
 */
public interface IAimlPerson extends IAimlElement {
}
