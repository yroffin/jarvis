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
 * The lowercase element tells the AIML interpreter to render the contents of
 * the element in lowercase, as defined (if defined) by the locale indicated by
 * the specified language (if specified).
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:lowercase>
 * 
 * <!-- Content: aiml-template-elements -->
 * 
 * </aiml:lowercase>
 * 
 * If no character in this string has a different lowercase version, based on
 * the Unicode standard, then the original string is returned.
 * 
 * See Unicode Case Mapping for implementation suggestions.
 */
public interface IAimlLowercase extends IAimlElement {
}
