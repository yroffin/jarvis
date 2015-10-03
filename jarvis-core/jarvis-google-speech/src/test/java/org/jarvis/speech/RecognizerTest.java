package org.jarvis.speech;

import java.io.File;
import java.io.IOException;

import org.jarvis.speech.recognizer.IRecognizer;
import org.jarvis.speech.recognizer.impl.RecognizerImpl;
import org.junit.Test;

public class RecognizerTest {

	@Test
	public void test() throws IOException {
		IRecognizer engine = new RecognizerImpl("fr");
		File flacFile = new File("src/test/resources/jasper.wav");
		engine.transform(flacFile);
	}

}
