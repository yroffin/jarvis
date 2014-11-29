package org.jarvis.main.engine;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jarvis.main.engine.impl.AimlCoreEngineImpl;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.junit.Test;

public class AimlRegisterTest {

	@Test
	public void testEngine() throws IOException, AimlParsingError {
		IAimlCoreEngine engine = new AimlCoreEngineImpl(Arrays.asList(
				"data/system.aiml", "data/knock.aiml"));

		engine.set("name", "Nancy");
		List<IAimlHistory> answer = engine.ask("Knock knock.");
		assertEquals("Who is there?", answer.get(0).getAnswer());
	}

	@Test
	public void testEngineError() throws IOException, AimlParsingError {
		new AimlCoreEngineImpl(Arrays.asList("core/default/error.xml"));
	}

}
