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

package org.jarvis.main.engine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlCategory;
import org.jarvis.main.model.parser.history.IAimlHistory;

/**
 * AIML engine
 */
public interface IAimlCoreEngine {
	/**
	 * retrieve transaction monitor
	 * @return
	 */
	IAimlCoreTransactionMonitor getTransactionMonitor();

	/**
	 * the categories currently loaded
	 * 
	 * @return
	 */
	public List<IAimlCategory> getCategories();

	/**
	 * bot properties
	 * 
	 * @param key
	 * @return
	 */
	public Object getBot(String key);

	/**
	 * bot properties
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object setBot(String key, Object value);

	/**
	 * properties get
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key);

	/**
	 * property set
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Object set(String key, Object value);

	/**
	 * add a new file in system
	 * 
	 * @param resource
	 * @return
	 */
	public void register(File resource);

	/**
	 * parse the resources
	 */
	public void parse() throws AimlParsingError;

	/**
	 * implement default bot behaviour
	 * 
	 * @param sentence
	 * @return
	 * @throws AimlParsingError
	 */
	public List<IAimlHistory> ask(String sentence) throws AimlParsingError;

	/**
	 * exchange history
	 * 
	 * @return
	 */
	public Stack<List<IAimlHistory>> getHistory();

	/**
	 * return that history (not last, but last last)
	 * 
	 * @return
	 */
	public IAimlHistory getThatHistory();

	/**
	 * return that history
	 * 
	 * @return
	 */
	public List<IAimlHistory> getThatsHistory();

	/**
	 * register a new resource
	 * 
	 * @param resource
	 * @throws IOException
	 */
	void register(String resource) throws IOException;
}
