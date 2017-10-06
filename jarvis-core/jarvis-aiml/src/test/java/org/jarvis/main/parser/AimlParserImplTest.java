package org.jarvis.main.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.IAimlRender;
import org.jarvis.main.model.parser.IAimlRepository;
import org.junit.Test;

public class AimlParserImplTest {

	/**
	 * utility
	 * @param result
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	protected File create(IAimlRender result, String filename) throws IOException {
		File file = new File(filename);
		(new File(file.getParent())).mkdirs();
		PrintStream outputStream = new PrintStream(file);
		outputStream .write((result.toAiml(new StringBuilder())).toString().getBytes());
		outputStream.close();
		return file;
	}

	/**
	 * @throws AimlParsingError
	 */
	@Test
	public void testSimpleLoad() throws AimlParsingError {
		AimlParserImpl.parse(new File("src/test/aiml/default.aiml"));
	}

	/**
	 * @throws AimlParsingError
	 * @throws IOException
	 */
	@Test
	public void testAnotherSimpleLoad() throws AimlParsingError, IOException {
		IAimlRepository result = AimlParserImpl.parse(new File("src/test/aiml/french_aiml_publish/atomique_ed.aiml"));
		assertEquals(
				"",
				FileUtils.readFileToString(new File("src/test/resources/files/atomique_ed.aiml")),
				result.toAiml(new StringBuilder()).toString());
	}
}
