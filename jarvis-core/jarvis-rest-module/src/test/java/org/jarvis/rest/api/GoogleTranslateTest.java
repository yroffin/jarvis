package org.jarvis.rest.api;

import java.io.IOException;

import org.junit.Test;

public class GoogleTranslateTest {

	@Test
	public void test() throws IOException {
		GoogleTranslate.translate("AIzaSyBPKiS0Bd3ogPIZ_FVRVHFE8urti9eEy7U", "test", "fr");
	}

}
