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
package org.jarvis.main.model.parser.template.format;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * The sentence element tells the AIML interpreter to render the contents of the
 * element such that the first letter of each sentence is in uppercase, as
 * defined (if defined) by the locale indicated by the specified language (if
 * specified). Sentences are interpreted as strings whose last character is the
 * period or full-stop character .. If the string does not contain a ., then the
 * entire string is treated as a sentence.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:sentence>
 * 
 * <!-- Content: aiml-template-elements -->
 * 
 * </aiml:sentence>
 * 
 * If no character in this string has a different uppercase version, based on
 * the Unicode standard, then the original string is returned.
 * 
 * See Unicode Case Mapping for implementation suggestions.
 */
public interface IAimlSentence extends IAimlElement {
}
