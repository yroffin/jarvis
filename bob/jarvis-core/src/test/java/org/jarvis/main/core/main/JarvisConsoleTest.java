package org.jarvis.main.core.main;

import java.io.IOException;

import org.jarvis.main.exception.AimlParsingError;
import org.junit.Test;

public class JarvisConsoleTest {

	@Test
	public void test() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, AimlParsingError,
			IOException {
		String[] args = new String[1];
		args[0] = "jarvis.txt";
		JarvisConsole.main(args);
	}

	@Test
	public void testDirect() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, AimlParsingError,
			IOException {
		String[] args = new String[1];
		args[0] = "jarvis.txt";
		JarvisConsole.direct(args, "test jarvis".split(","));
	}

}
