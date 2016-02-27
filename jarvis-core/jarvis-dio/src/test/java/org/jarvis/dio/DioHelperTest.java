package org.jarvis.dio;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

/**
 * DioHelper test
 */
public class DioHelperTest {

	/**
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	@Test
	@Ignore
	public void testSwitchOn() throws InterruptedException, IOException {
		DioHelper dioHelper = new DioHelper();
		dioHelper.pin(17).sender(8217034).interruptor(1).switchOn();
	}

}
