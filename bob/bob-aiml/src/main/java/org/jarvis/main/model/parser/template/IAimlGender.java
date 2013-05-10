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
 * The gender element instructs the AIML interpreter to:
 * 
 * replace male-gendered words in the result of processing the contents of the
 * gender element with the grammatically-corresponding female-gendered words;
 * and replace female-gendered words in the result of processing the contents of
 * the gender element with the grammatically-corresponding male-gendered words.
 * The definition of "grammatically-corresponding" is left up to the
 * implementation.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:gender>
 * 
 * <!-- Contents: aiml-template-elements -->
 * 
 * </aiml:gender>
 * 
 * Historically, implementations of gender have exclusively dealt with pronouns,
 * likely due to the fact that most AIML has been written in English. However,
 * the decision about whether to transform gender of other words is left up to
 * the implementation.
 */
public interface IAimlGender extends IAimlElement {
}
