package org.jarvis.main.parser;

import java.io.File;

import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlRepository;
import org.junit.Test;

public class AimlParserImplTest {

	@Test
	public void testSimpleLoad() throws AimlParsingError {
		IAimlRepository result = AimlParserImpl.parse(new File("src/test/aiml/default.aiml"));
		System.err.println(""+result);
	}

	@Test
	public void testAnotherSimpleLoad() throws AimlParsingError {
		IAimlRepository result = AimlParserImpl.parse(new File("src/test/aiml/french_aiml_publish/atomique_ed.aiml"));
		System.err.println(""+result);
	}
}
