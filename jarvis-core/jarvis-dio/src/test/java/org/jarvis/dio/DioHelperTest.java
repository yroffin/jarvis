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
		dioHelper.pin(1).sender(123456789).interruptor(4).switchOn();
	}

}
