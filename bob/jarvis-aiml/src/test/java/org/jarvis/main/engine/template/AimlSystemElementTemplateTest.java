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
import java.util.Date;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlSystemElementTemplateTest {

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
	public void testSimpleDate() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/system-defined/system.aiml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("Get date system.");
		assertEquals(
				("system " + (new Date()) + " localhost").substring(0, 22),
				answer.get(0).getAnswer().substring(0, 22));
		assertEquals(("system " + (new Date()) + " localhost").substring(27),
				answer.get(0).getAnswer().substring(27));
	}

	/**
	 * Star test
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSize() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/system-defined/system.aiml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("Get size and version");
		assertEquals("size: 4 version: 1.0.1", answer.get(0).getAnswer());
		answer = engine.ask("Get upper and lower");
		assertEquals("upper: X lower: x", answer.get(0).getAnswer());
		answer = engine.ask("Get upper and lower aAAAsdqdqsDQSDqd");
		assertEquals("upper: AAAASDQDQSDQSDQD lower: aaaasdqdqsdqsdqd", answer
				.get(0).getAnswer());
	}
}