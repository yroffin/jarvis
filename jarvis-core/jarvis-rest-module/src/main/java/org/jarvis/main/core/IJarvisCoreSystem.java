/**
 *  Copyright 2015 Yannick Roffin
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

package org.jarvis.main.core;

import java.io.IOException;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;

/**
 * interface of each module
 */
public interface IJarvisCoreSystem {

	/**
	 * release a module
	 */
	public void release();

	/**
	 * @param botname
	 * @param aiml
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	public void initialize(String botname, String aiml)
			throws AimlParsingError, IOException;

	/**
	 * @param string
	 * @return List<IAimlHistory>
	 * @throws AimlParsingError
	 */
	public List<IAimlHistory> ask(String string) throws AimlParsingError;

	/**
	 * @param sentence
	 * @return List<IAimlHistory>
	 * @throws AimlParsingError
	 */
	public List<IAimlHistory> chat(String sentence) throws AimlParsingError;

	/**
	 * @param value
	 * @throws IOException
	 */
	public void speak(String value) throws IOException;

	/**
	 * @return IAimlCoreEngine
	 */
	IAimlCoreEngine getEngine();

	/**
	 * @param sentence
	 * @return List<IAimlHistory>
	 * @throws AimlParsingError
	 */
	List<IAimlHistory> askSilent(String sentence) throws AimlParsingError;
}