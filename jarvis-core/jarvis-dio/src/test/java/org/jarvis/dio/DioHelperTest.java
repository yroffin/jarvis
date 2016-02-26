package org.jarvis.dio;

import org.junit.Test;

/**
 * DioHelper test
 */
public class DioHelperTest {

	/**
	 * @throws InterruptedException
	 */
	@Test
	public void testSwitchOn() throws InterruptedException {
		DioHelper dioHelper = new DioHelper();
		dioHelper.pin(17).sender(8217034).interruptor(1).switchOn();
	}

}
