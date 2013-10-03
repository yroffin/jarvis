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
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlCoreEngineSraiTest {

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
	 * Symbolic reduction refers to the process of simplifying complex
	 * grammatical forms into simpler ones. Usually, the atomic patterns in
	 * categories storing robot knowledge are stated in the simplest possible
	 * terms, for example we tend to prefer patterns like "WHO IS SOCRATES" to
	 * ones like "DO YOU KNOW WHO SOCRATES IS" when storing biographical
	 * information about Socrates.
	 * 
	 * Many of the more complex forms reduce to simpler forms using AIML
	 * categories designed for symbolic reduction:
	 * 
	 * <category> <pattern>DO YOU KNOW WHO * IS</pattern> <template><srai>WHO IS
	 * <star/></srai></template> </category>
	 * 
	 * Whatever input matched this pattern, the portion bound to the wildcard *
	 * may be inserted into the reply with the markup <star/>. This category
	 * reduces any input of the form "Do you know who X is?" to "Who is X?"
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiReduction() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-reduction.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("who is bruno ?");
		assertEquals("bruno is someone, but i don't know him.", answer.get(0)
				.getAnswer());
		answer = engine.ask("do you know who bruno is ?");
		assertEquals("bruno is someone, but i don't know him.", answer.get(0)
				.getAnswer());
		
		for(IAimlCoreTransaction tx : engine.getTransactionMonitor().getTransactions()) {
			for(ICategoryStack category : tx.getCategories()) {
				System.err.println(category);
			}
		}
	}

	/**
	 * Many individual sentences may be reduced to two or more subsentences, and
	 * the reply formed by combining the replies to each. A sentence beginning
	 * with the word "Yes" for example, if it has more than one word, may be
	 * treated as the subsentence "Yes." plus whatever follows it.
	 * 
	 * <category> <pattern>YES *</pattern> <template><srai>YES</srai>
	 * <sr/></template> </category>
	 * 
	 * The markup <sr/> is simply an abbreviation for <srai><star/></srai>.
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiDivideAndConquer() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-divide-conquer.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("yes mister");
		assertEquals(
				"Divide and Conquer: Catch All: yes and Catch All: mister",
				answer.get(0).getAnswer());
	}

	/**
	 * The AIML 1.01 standard does not permit more than one pattern per
	 * category. Synonyms are perhaps the most common application of <srai>.
	 * Many ways to say the same thing reduce to one category, which contains
	 * the reply:
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiSynonym() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-synonym.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("hola");
		assertEquals("Hi there!", answer.get(0).getAnswer());
	}

	/**
	 * The single most common client spelling mistake is the use of "your" when
	 * "you�re" or "you are" is intended. Not every occurrence of "your" however
	 * should be turned into "you�re." A small amount of grammatical context is
	 * usually necessary to catch this error.
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiGrammar() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-grammar.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("your a sheep");
		assertEquals(
				"I think you mean \"you're\" or \"you are\" not \"your.\" I think your are a sheep",
				answer.get(0).getAnswer());
	}

	/**
	 * Frequently we would like to write an AIML template which is activated by
	 * the appearance of a keyword anywhere in the input sentence. The general
	 * format of four AIML categories is illustrated by this example borrowed
	 * from ELIZA
	 * 
	 * _ is not implemented for now
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiKeyword() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-keyword.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("something about my mother");
		assertEquals(" Tell me more about your family. ", answer.get(0)
				.getAnswer());
	}

	/**
	 * It is possible to write conditional branches in AIML, using only the
	 * <srai> tag. Consider three categories:
	 * 
	 * <category> <pattern>WHO IS HE</pattern> <template><srai>WHOISHE <get
	 * name="he"/></srai></template> </category>
	 * 
	 * <category> <pattern>WHOISHE *</pattern> <template>He is <get
	 * name="he"/>.</template> </category>
	 * 
	 * <category> <pattern>WHOISHE UNKNOWN</pattern> <template>I don�t know who
	 * he is.</template> </category>
	 * 
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleSraiCond() throws AimlParsingError {
		IAimlCoreEngine engine = instance("src/test/resources/core/srai/srai-conditional.xml");
		List<IAimlHistory> answer = null;

		answer = engine.ask("who is he");
		assertEquals("He is null.", answer.get(0).getAnswer());

		engine.set("he", "jarvis");
		answer = engine.ask("who is he");
		assertEquals("He is jarvis.", answer.get(0).getAnswer());
	}
}