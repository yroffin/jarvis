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

package org.jarvis.main.engine.condition;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

/**
 * test
 */
public class AimlAliceTest {

	/**
	 * internal utility
	 * 
	 * @param resources
	 * @return IAimlCoreEngine
	 * @throws AimlParsingError
	 */
	public IAimlCoreEngine instance(String resources) throws AimlParsingError {
		IAimlCoreEngine core = new AimlCoreEngineImpl();
		core.register(new File(resources));
		core.parse();
		return core;
	}

	/**
	 * Block condition
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleCondition1() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/condition/conditions.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");

		answer = engine.ask("Test condition 1");
		assertEquals(false,
				answer.get(0).getAnswer()
						.contains("You are the true botmaster"));

		assertEquals(
				false,
				answer.get(0).getAnswer()
						.contains("Impostor! You are not my real botmaster"));

		engine.set("ip", "localhost");

		answer = engine.ask("Test condition 1");
		assertEquals(true,
				answer.get(0).getAnswer()
						.contains("You are the true botmaster"));

		engine.set("name", "i'm wallace");

		answer = engine.ask("Test condition 1");
		assertEquals(
				true,
				answer.get(0).getAnswer()
						.contains("Impostor! You are not my real botmaster"));
	}

	/**
	 * Block condition
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleCondition2() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/condition/conditions.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");

		engine.set("gender", "female");
		answer = engine.ask("i am blond 1");
		assertEquals("You sound very attractive.", answer.get(0).getAnswer());
		engine.set("gender", "male");
		answer = engine.ask("i am blond 1");
		assertEquals("You sound very handsome.", answer.get(0).getAnswer());
	}

	/**
	 * Block condition
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleCondition3() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/condition/conditions.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");

		engine.set("gender", "female");
		answer = engine.ask("i am blond 2");
		assertEquals("You sound very attractive.", answer.get(0).getAnswer());
		engine.set("gender", "male");
		answer = engine.ask("i am blond 2");
		assertEquals("You sound very handsome.", answer.get(0).getAnswer());
	}

	/**
	 * Single condition
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleCondition4() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/condition/conditions.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");

		engine.set("gender", "female");
		answer = engine.ask("i am blond 3");
		assertEquals("You sound very attractive.", answer.get(0).getAnswer());
		engine.set("gender", "male");
		answer = engine.ask("i am blond 3");
		assertEquals("You sound very handsome.", answer.get(0).getAnswer());
	}

	/**
	 * Single condition
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleCondition5() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/condition/conditions.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");

		engine.set("gender", "female");
		answer = engine.ask("i am blond 4");
		assertEquals("You sound very ...attractive.", answer.get(0)
				.getAnswer());
		engine.set("gender", "male");
		answer = engine.ask("i am blond 4");
		assertEquals("You sound very ...handsome.", answer.get(0)
				.getAnswer());
	}
}