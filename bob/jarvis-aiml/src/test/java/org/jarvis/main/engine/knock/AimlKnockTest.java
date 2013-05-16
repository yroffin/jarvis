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

package org.jarvis.main.engine.knock;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlKnockTest {

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
	 * Alice BOT
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleKnock() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/knock/knock.aiml");
		List<IAimlHistory> answer = null;

		engine.set("name", "Nancy");
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Banana.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Banana.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Orange.");
		assertEquals("Orange who?", answer.get(0).getAnswer());
		answer = engine.ask("Orange you glad I didn’t say banana.");
		assertEquals("Ha ha very funny, Nancy.", answer.get(0).getAnswer());
	}

	/**
	 * Alice BOT
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleInput() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/knock/input.aiml");
		List<IAimlHistory> answer = null;

		engine.set("name", "Nancy");
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Banana. Knock knock.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		assertEquals("Who is there?", answer.get(1).getAnswer());
		answer = engine.ask("Banana.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Orange.");
		assertEquals("Orange who?", answer.get(0).getAnswer());
		answer = engine.ask("Orange you glad I didn’t say banana.");

		answer = engine.ask("What i said at first ?");
		assertEquals("Your previous input was Knock knock.", answer.get(0)
				.getAnswer());
		answer = engine.ask("What i said at second ?");
		assertEquals("Your previous input was Banana.", answer.get(0)
				.getAnswer());
		answer = engine.ask("What i said at second, second sentence ?");
		assertEquals("Your previous input was Knock knock.", answer.get(0)
				.getAnswer());
	}

	/**
	 * Alice BOT
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleThat() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/knock/input.aiml");
		List<IAimlHistory> answer = null;

		engine.set("name", "Nancy");
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Banana. Knock knock.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		assertEquals("Who is there?", answer.get(1).getAnswer());
		answer = engine.ask("Banana.");
		assertEquals("Banana who?", answer.get(0).getAnswer());
		answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
		answer = engine.ask("Orange.");
		assertEquals("Orange who?", answer.get(0).getAnswer());
		answer = engine.ask("Orange you glad I didn’t say banana.");

		answer = engine.ask("What you said first ?");
		assertEquals("That was Who is there?.", answer.get(0).getAnswer());
		answer = engine.ask("What you said second ?");
		assertEquals("That was Ha ha very funny, Nancy..", answer.get(0)
				.getAnswer());
		answer = engine.ask("What you said three ?");
		assertEquals("That was Who is there?.", answer.get(0).getAnswer());
	}
}