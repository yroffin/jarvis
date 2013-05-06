package org.jarvis.main.parser;

import java.io.File;

import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlRepository;
import org.junit.Test;

public class AimlParserImplTest {

	@Test
	public void testSimpleLoad() throws AimlParsingError {
		AimlParserImpl parser = new AimlParserImpl(new File("src/test/aiml/default.aiml"));
		IAimlRepository result = parser.parse();
		System.err.println(""+result);
	}
}
