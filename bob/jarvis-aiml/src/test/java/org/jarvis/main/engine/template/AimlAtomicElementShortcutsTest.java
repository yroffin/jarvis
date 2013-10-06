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

package org.jarvis.main.engine.template;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlProperty;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlAtomicElementShortcutsTest {

	/**
	 * internal utility
	 * 
	 * @param resources
	 * @return
	 * @throws AimlParsingError
	 */
	public IAimlCoreEngine instance(String resources) throws AimlParsingError {
		IAimlCoreEngine core = new AimlCoreEngineImpl();
		core.register(new File(resources));
		core.parse();
		return core;
	}

	/**
	 * Star test
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleShortcuts() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/shortcut-elements/shortcuts.aiml");
		List<IAimlHistory> answer = null;

		engine.getAiml().accept(new AimlProperty("topic", " a simple topic "));
		answer = engine.ask("Hello.");
		assertEquals("Hi there!", answer.get(0).getAnswer());
		answer = engine.ask("Hola Hello.");
		assertEquals(
				"A: In spanish sr: Hi there!person: Hello person two: hello B: In spanish person: Abcd person two: efg",
				answer.get(0).getAnswer());
	}
}