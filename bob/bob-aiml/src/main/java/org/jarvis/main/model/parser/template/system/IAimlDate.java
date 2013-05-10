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
 * The date element tells the AIML interpreter that it should substitute the
 * system local date and time. No formatting constraints on the output are
 * specified.
 * 
 * The date element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:date/>
 */
public interface IAimlDate extends IAimlElement {

}
