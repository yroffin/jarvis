/**
 * 
 */
package org.jarvis.core.resources.api.scenario;

import java.util.ArrayList;
import java.util.List;

import org.jarvis.core.model.bean.GenericBean;
import org.jarvis.core.model.bean.scenario.ScenarioBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.plugin.ScriptPluginRest;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.resources.api.href.ApiHrefBlockBlockResources;
import org.jarvis.core.resources.api.href.ApiHrefBlockScriptPluginResources;
import org.jarvis.core.resources.api.href.ApiHrefScenarioBlockResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.services.ApiService;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.test.JsonTestFactory;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * test
 */
public class ApiBlockResourcesTest {

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

	ApiBlockResources apiBlockResources;
	ApiHrefScenarioBlockResources apiHrefScenarioBlockResources;
	
	ApiScenarioResources apiScenarioResources;
	ApiHrefBlockScriptPluginResources apiHrefBlockScriptPluginResources;
	ApiScriptPluginResources apiScriptPluginResources;
	ApiHrefBlockBlockResources apiHrefBlockBlockResources;
	ApiService<?> apiService;
	ApiNeo4Service apiNeo4Service;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		apiScenarioResources = new ApiScenarioResources();
		apiHrefScenarioBlockResources = Mockito.mock(ApiHrefScenarioBlockResources.class);

		/**
		 * retrieve condition
		 */
		Mockito.when(apiHrefScenarioBlockResources.findAll(Matchers.any())).then(new Answer<List<GenericEntity>>() {

			@Override
			public List<GenericEntity> answer(InvocationOnMock invocation) throws Throwable {
				List<GenericEntity> result = new ArrayList<GenericEntity>();
				GenericEntity e = new GenericEntity();
				e.id = "12000";
				result.add(e);
				return result;
			}
			
		});

		apiBlockResources = new ApiBlockResources();
		apiService = Mockito.mock(ApiService.class);
		apiNeo4Service = Mockito.mock(ApiNeo4Service.class);
		apiHrefBlockScriptPluginResources = Mockito.mock(ApiHrefBlockScriptPluginResources.class);
		apiScriptPluginResources = Mockito.mock(ApiScriptPluginResources.class);
		apiHrefBlockBlockResources = Mockito.mock(ApiHrefBlockBlockResources.class);

		ReflectionTestUtils.setField(apiBlockResources, "apiService", apiService);
		ReflectionTestUtils.setField(apiBlockResources, "apiNeo4Service", apiNeo4Service);
		ReflectionTestUtils.setField(apiBlockResources, "apiHrefBlockScriptPluginResources", apiHrefBlockScriptPluginResources);
		ReflectionTestUtils.setField(apiBlockResources, "apiScriptPluginResources", apiScriptPluginResources);
		ReflectionTestUtils.setField(apiBlockResources, "apiHrefBlockBlockResources", apiHrefBlockBlockResources);

		ReflectionTestUtils.setField(apiScenarioResources, "apiHrefScenarioBlockResources", apiHrefScenarioBlockResources);
		ReflectionTestUtils.setField(apiScenarioResources, "apiBlockResources", apiBlockResources);
		ReflectionTestUtils.setField(apiScenarioResources, "apiService", apiService);

		/**
		 * retrieve condition
		 */
		Mockito.when(apiHrefBlockScriptPluginResources.findAllConditions(Matchers.any())).then(new Answer<List<GenericEntity>>() {

			@Override
			public List<GenericEntity> answer(InvocationOnMock invocation) throws Throwable {
				List<GenericEntity> result = new ArrayList<GenericEntity>();
				GenericEntity e = new GenericEntity();
				e.id = "10000";
				result.add(e);
				return result;
			}
			
		});

		/**
		 * get detail script
		 */
		Mockito.when(apiScriptPluginResources.doGetById("10000")).then(new Answer<ScriptPluginRest>() {

			@Override
			public ScriptPluginRest answer(InvocationOnMock invocation) throws Throwable {
				ScriptPluginRest result = new ScriptPluginRest();
				result.id = "10000";
				result.name = "test script";
				return result;
			}
			
		});

		/**
		 * get detail script
		 */
		Mockito.when(apiService.getById("10000")).then(new Answer<GenericBean>() {

			@Override
			public GenericBean answer(InvocationOnMock invocation) throws Throwable {
				GenericBean result = new GenericBean();
				result.id = "10000";
				return result;
			}
			
		});

		Mockito.when(apiService.getById("12000")).then(new Answer<GenericBean>() {

			@Override
			public GenericBean answer(InvocationOnMock invocation) throws Throwable {
				GenericBean result = new GenericBean();
				result.id = "12000";
				return result;
			}
			
		});

		/**
		 * find then script
		 */
		Mockito.when(apiHrefBlockScriptPluginResources.findAllThen(Matchers.any())).then(new Answer<List<GenericEntity>>() {

			@Override
			public List<GenericEntity> answer(InvocationOnMock invocation) throws Throwable {
				List<GenericEntity> result = new ArrayList<GenericEntity>();
				GenericEntity e = new GenericEntity();
				e.id = "10000";
				result.add(e);
				return result;
			}
			
		});

		/**
		 * find then block
		 */
		Mockito.when(apiHrefBlockBlockResources.findAllThen(Matchers.any())).then(new Answer<List<GenericEntity>>() {

			@Override
			public List<GenericEntity> answer(InvocationOnMock invocation) throws Throwable {
				List<GenericEntity> result = new ArrayList<GenericEntity>();
				GenericEntity e = new GenericEntity();
				e.id = "10000";
				result.add(e);
				return result;
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
	 * @throws Exception 
	 */
	@Test
	public void testCase1() throws Exception {
		ScenarioBean scenario = new ScenarioBean();
		ResourcePair result = apiScenarioResources.doRealTask(scenario, new GenericMap(), TaskType.RENDER);
		String scenarioActual = result.getValue();
		String scenarioExpected = JsonTestFactory.loadFromClasspath("case1-expected.json");
		Assert.assertEquals(scenarioExpected, scenarioActual);
	}

}
