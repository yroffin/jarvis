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
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jarvis.main.engine.transform.IAimlTransform;
import org.jarvis.main.engine.transform.impl.AimlTranformImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.transform.ITransformedItem;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlTranformTest {
	Logger	logger	= LoggerFactory.getLogger(AimlTranformTest.class);

	/**
	 * Since input normalization can have any of 1, 2 or 3 stages in a given
	 * AIML interpreter, results of normalization may vary widely among
	 * different AIML interpreters. Here we show examples of input normalization
	 * in a typical English-language-oriented AIML interpreter that includes the
	 * full complement of normalizations.
	 * 
	 * input
	 * 
	 * substitution normalized form
	 * 
	 * sentence-splitting normalized form
	 * 
	 * pattern-fitting normalized form
	 * 
	 * "What time is it?"
	 * 
	 * "What time is it?"
	 * 
	 * "What time is it"
	 * 
	 * "WHAT TIME IS IT"
	 * 
	 * "Quickly, go to http://alicebot.org!"
	 * 
	 * "Quickly, go to http://alicebot dot org!"
	 * 
	 * "Quickly, go to http://alicebot dot org"
	 * 
	 * "QUICKLY GO TO HTTP ALICEBOT DOT ORG"
	 * 
	 * ":-) That's funny."
	 * 
	 * "That is funny."
	 * 
	 * "That is funny"
	 * 
	 * "THAT IS FUNNY"
	 * 
	 * "I don't know. Do you, or will you, have a robots.txt file?"
	 * 
	 * "I do not know. Do you, or will you, have a robots dot txt file?"
	 * 
	 * "I do not know"
	 * 
	 * "Do you, or will you, have a robots dot txt file"
	 * 
	 * "I DO NOT KNOW"
	 * 
	 * "DO YOU OR WILL YOU HAVE A ROBOTS DOT TXT FILE"
	 * 
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	@Test
	public void testNormalizationExamples() throws AimlParsingError,
			IOException {
		IAimlTransform tx = new AimlTranformImpl();

		String[] inputs = FileUtils.readFileToString(
				new File("src/test/resources/inputs/inputs.txt")).split("\r\n");
		String[] outputs = FileUtils.readFileToString(
				new File("src/test/resources/inputs/outputs.txt"))
				.split("\r\n");

		int output = 0;
		for (int i = 0; i < inputs.length; i++) {
			logger.info("Parsing [" + inputs[i] + "]");
			List<ITransformedItem> trans = tx.transform(inputs[i]);
			for (int j = 0; j < trans.size(); j++) {
				assertEquals(outputs[output++], trans.get(j).toString());
			}
		}
	}
}
