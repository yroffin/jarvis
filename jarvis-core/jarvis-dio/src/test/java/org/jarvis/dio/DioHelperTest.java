package org.jarvis.dio;

import org.junit.Test;

public class DioHelperTest {

	@Test
	public void testSwitchOn() throws InterruptedException {
		DioHelper.main(new String[] {"1","23456789","4","on"});
	}

}
