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
 * The star element indicates that an AIML interpreter should substitute the
 * value "captured" by a particular wildcard from the pattern-specified portion
 * of the match path when returning the template.
 * 
 * The star element has an optional integer index attribute that indicates which
 * wildcard to use. The minimum acceptable value for the index is "1" (the first
 * wildcard), and the maximum acceptable value is equal to the number of
 * wildcards in the pattern.
 * 
 * An AIML interpreter should raise an error if the index attribute of a star
 * specifies a wildcard that does not exist in the category element's pattern.
 * Not specifying the index is the same as specifying an index of "1".
 * 
 * The star element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:star index = single-integer-index />
 */
public interface IAimlStar extends IAimlElement {
	public void setIndex(int index);
}
