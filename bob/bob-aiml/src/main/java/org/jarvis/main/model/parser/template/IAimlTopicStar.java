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
 * The thatstar element tells the AIML interpreter that it should substitute the
 * contents of a wildcard from a pattern-side that element.
 * 
 * The thatstar element has an optional integer index attribute that indicates
 * which wildcard to use; the minimum acceptable value for the index is "1" (the
 * first wildcard).
 * 
 * An AIML interpreter should raise an error if the index attribute of a star
 * specifies a wildcard that does not exist in the that element's pattern
 * content. Not specifying the index is the same as specifying an index of "1".
 * 
 * The thatstar element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:thatstar index = single-integer-index />
 */
public interface IAimlTopicStar extends IAimlElement {
}
