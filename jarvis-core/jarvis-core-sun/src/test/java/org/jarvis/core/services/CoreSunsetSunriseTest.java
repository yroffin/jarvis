/**
 * 
 */
package org.jarvis.core.services;

import org.jarvis.core.model.rest.sun.SunApiRest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author kazoar
 *
 */
public class CoreSunsetSunriseTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	Environment environment;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		environment = Mockito.mock(Environment.class);
		Mockito.when(environment.getProperty("jarvis.sunset.sunrise.url")).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "http://api.sunrise-sunset.org";
			}
			
		});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * simple test
	 */
	@Test
	public void testApiCall() {
		CoreSunsetSunrise coreSunsetSunrise = new CoreSunsetSunrise();
		ReflectionTestUtils.setField(coreSunsetSunrise, "env", environment);
		coreSunsetSunrise.init();
		SunApiRest result = coreSunsetSunrise.get("48.3053133", "-1.7030455,13");
		System.err.println(result);
	}

}
