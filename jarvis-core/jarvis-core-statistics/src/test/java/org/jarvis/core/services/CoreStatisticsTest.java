/**
 * 
 */
package org.jarvis.core.services;

import org.common.core.type.GenericMap;
import org.jarvis.core.model.rest.GenericEntity;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author kazoar
 *
 */
public class CoreStatisticsTest {

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
		Mockito.when(environment.getProperty("jarvis.elasticsearch.url")).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return "http://localhost:9200";
			}
			
		});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private class myData extends GenericEntity {
		@JsonProperty("date")
		DateTime date = new DateTime();
		@JsonProperty("i")
		int number = new DateTime().getMillisOfDay();
	}
	
	/**
	 * simple test
	 */
	@Test
	@Ignore
	public void test() {
		CoreStatistics statistics = new CoreStatistics();
		ReflectionTestUtils.setField(statistics, "env", environment);
		statistics.init();
		
		for(int i = 0;i<100;i++) {
			myData d = new myData();
			GenericMap stat = new GenericMap();
			stat.put("entity", d.id);
			statistics.write(stat, d.getClass().getPackage().getName(), d.getClass().getSimpleName());
		}
	}

}
