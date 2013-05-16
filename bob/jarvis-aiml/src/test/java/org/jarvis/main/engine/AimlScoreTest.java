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

import java.util.ArrayList;

import org.jarvis.main.model.impl.transform.TransformedItemImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlScoreTest {
	Logger	logger	= LoggerFactory.getLogger(AimlScoreTest.class);

	@Test
	public void testSimpleScoreUnderscore1() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS ALICE");
		TransformedItemImpl b = new TransformedItemImpl("_ ALICE");
		assertEquals(2, a.score(b));
		assertEquals("[do you know who bob is]",
				a.star(b, new ArrayList<String>()).toString());
	}

	@Test
	public void testSimpleScoreUnderscore2() {
		TransformedItemImpl a = new TransformedItemImpl("ALICE IS AT THE TOP");
		TransformedItemImpl b = new TransformedItemImpl("ALICE _");
		assertEquals(2, a.score(b));
		assertEquals("[is at the top]", a.star(b, new ArrayList<String>())
				.toString());
	}

	@Test
	public void testSimpleScoreSystem1() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("DO YOU KNOW WHO * IS");
		assertEquals(6, a.score(b));
		assertEquals("[bob]", a.star(b, new ArrayList<String>()).toString());
	}

	@Test
	public void testSimpleScoreSystem2() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("DO * IS");
		assertEquals(3, a.score(b));
		assertEquals("[you know who bob]", a.star(b, new ArrayList<String>())
				.toString());
	}

	@Test
	public void testSimpleScoreSystem3() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("DO YOU * WHO * IS");
		assertEquals(6, a.score(b));
		assertEquals("[know, bob]", a.star(b, new ArrayList<String>())
				.toString());
	}

	@Test
	public void testSimpleScoreSystem4() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("*");
		assertEquals(1, a.score(b));
		assertEquals("[do you know who bob is]",
				a.star(b, new ArrayList<String>()).toString());
	}

	@Test
	public void testSimpleScoreSystem5() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("ANOTHER");
		assertEquals(-1, a.score(b));
	}

	@Test
	public void testSimpleScoreSystem6() {
		TransformedItemImpl a = new TransformedItemImpl(
				"DO YOU KNOW WHO BOB IS");
		TransformedItemImpl b = new TransformedItemImpl("HAVE YOU * WHO * IS");
		assertEquals(-1, a.score(b));
	}

}
