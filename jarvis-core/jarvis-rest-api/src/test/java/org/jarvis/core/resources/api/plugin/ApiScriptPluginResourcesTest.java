/**
 *  Copyright 2017 Yannick Roffin
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.jarvis.core.resources.api.plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.common.core.type.GenericMap;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * ApiScriptPluginResourcesTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
@Configuration
public class ApiScriptPluginResourcesTest {

	@Autowired
	private ApiScriptPluginResources apiScriptPluginResources;

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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * test uml task
	 * @throws IOException 
	 */
	@Test
	public void testUml() throws IOException {
		GenericMap args = new GenericMap();
		ScriptPluginBean bean = new ScriptPluginBean();
		bean.name = "Plugin de test";
		GenericValue value = apiScriptPluginResources.doRealTask(bean, args, "uml");
		File output = new File(new File("target").getAbsolutePath() + '/' + "output.svg");
		PrintWriter out = new PrintWriter(output);
		out.write(value.asSvg());
		out.close();
		System.err.println(output.getAbsolutePath());
	}
}
