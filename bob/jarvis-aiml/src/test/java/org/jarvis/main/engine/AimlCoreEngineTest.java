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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.junit.Test;

public class AimlCoreEngineTest {

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

	@Test
	public void testSimpleCoreSystem() throws AimlParsingError {
		List<String> answer = instance(
				"src/test/resources/core/default/default.xml").ask("hello");
		assertEquals("Hi there!", answer.get(0));
	}

	@Test
	public void testSimpleCoreSystemAlt() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/default/default.xml");
		List<String> answer = null;
		for (int i = 0; i < 100; i++) {
			answer = engine.ask("any");
			boolean a = "What is your favorite movie?".compareTo(answer.get(0)) == 0;
			boolean b = "What is your name?".compareTo(answer.get(0)) == 0;
			boolean c = "Will you buy me a drink?".compareTo(answer.get(0)) == 0;
			assertEquals(a || b || c, true);
		}
	}

	@Test
	public void testSimpleCoreSystemSay() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/default/default.xml");
		List<String> answer = null;
		for (int i = 0; i < 10; i++) {
			answer = engine.ask("say a b or c");
			boolean a = "a".compareTo(answer.get(0)) == 0;
			boolean b = "b".compareTo(answer.get(0)) == 0;
			boolean c = "c".compareTo(answer.get(0)) == 0;
			assertEquals(answer.get(0), a || b || c, true);
		}
	}
}
