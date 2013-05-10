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
package org.jarvis.main.model.parser.category;

import org.jarvis.main.model.parser.IAimlElement;

/**
 * An element called bot, which may be considered a restricted version of get,
 * is used to tell the AIML interpreter that it should substitute the contents
 * of a "bot predicate". The value of a bot predicate is set at load-time, and
 * cannot be changed at run-time. The AIML interpreter may decide how to set the
 * values of bot predicate at load-time. If the bot predicate has no value
 * defined, the AIML interpreter should substitute an empty string.
 * 
 * The bot element has a required name attribute that identifies the bot
 * predicate.
 * 
 * The bot element does not have any content.
 * 
 * <!-- Category: aiml-template-elements -->
 * 
 * <aiml:bot
 */
public interface IAimlBot extends IAimlElement {

}
