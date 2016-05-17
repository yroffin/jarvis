package org.jarvis.core.services.cache;

import org.jarvis.core.resources.ResourceData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * class
 */
public class CoreEhcacheManagerTest {

	/**
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	CoreEhcacheManager coreEhcacheManager = new CoreEhcacheManager();
	
	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		coreEhcacheManager.init();
	}

	/**
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * default test
	 */
	@Test
	public void testDefault() {
		ResourceData resourceData = new ResourceData("test");
		resourceData.setString("value");
		coreEhcacheManager.put("test", resourceData);
		org.junit.Assert.assertEquals(coreEhcacheManager.contains("test"), true);
		ResourceData result = coreEhcacheManager.get("test");
		System.err.println(result);
	}

}
