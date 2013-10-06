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

package org.jarvis.main.engine.alice;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jarvis.main.engine.IAimlCoreEngine;
import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.impl.parser.AimlProperty;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlAliceTest {

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
	public void testSimpleAlice() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/alice/alice.aiml");
		List<IAimlHistory> answer = null;

		engine.getAiml().accept(new AimlProperty("topic", " ENDS WITH ALICE "));
		engine.setBot("name", "ALICE");
		answer = engine.ask("CALL ME BOTNAME");
		assertEquals("My name is ALICE too!\n\n", answer.get(0).getAnswer());
	}

	@Test
	public void testSimpleAliceHello() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/alice/alice.aiml");
		List<IAimlHistory> answer = null;

		engine.setBot("name", "ALICE");
		answer = engine.ask("HOW DID YOU HEAR ABOUT ALICE");
		engine.setLastAnswer("HOW DID YOU HEAR ABOUT ALICE");
		answer = engine.ask("In romania");
		assertEquals(" heard about ALICE from Romania. I can't say that many people hear about ALICE from  Romania .", answer.get(0).getAnswer());
	}
}