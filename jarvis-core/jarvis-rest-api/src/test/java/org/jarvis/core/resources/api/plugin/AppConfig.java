package org.jarvis.core.resources.api.plugin;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.plugin.CommandBean;
import org.jarvis.core.model.bean.plugin.ScriptPluginBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.resources.api.href.ApiHrefPluginCommandResources;
import org.jarvis.core.resources.api.plugins.ApiCommandResources;
import org.jarvis.core.resources.api.plugins.ApiScriptPluginResources;
import org.jarvis.core.services.CoreMoquette;
import org.jarvis.core.services.CoreStatistics;
import org.jarvis.core.services.neo4j.ApiNeo4Service;
import org.jarvis.core.type.CommandType;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * test
 */
@Configuration
public class AppConfig {
	
	@Bean
	MockBeanFactory mockBeanFactory() {
	    return new MockBeanFactory();
	}
	
	private static class MockBeanFactory extends InstantiationAwareBeanPostProcessorAdapter {
	    @Override
	    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
	    	if(beanName.equals("apiCommandResources")) {
	    		return false;
	    	}
	    	return true;
	    }
	}
	
	/**
	 * @return ApiScriptPluginResources
	 */
	@Bean
	public ApiScriptPluginResources apiScriptPluginResources() {
		return new ApiScriptPluginResources();
	}

	/**
	 * @return CoreStatistics
	 */
	@Bean
	public CoreStatistics coreStatistics() {
		return mock(CoreStatistics.class);
	}

	/**
	 * @return CoreMoquette
	 */
	@Bean
	public CoreMoquette coreMoquette() {
		return mock(CoreMoquette.class);
	}

	/**
	 * @return ApiNeo4Service
	 */
	@Bean
	public ApiNeo4Service apiNeo4Service() {
		return mock(ApiNeo4Service.class);
	}

	/**
	 * @return ApiCommandResources
	 * @throws TechnicalNotFoundException 
	 */
	@Bean
	public ApiCommandResources apiCommandResources() throws TechnicalNotFoundException {
		ApiCommandResources mocked = mock(ApiCommandResources.class);
		CommandBean commande1 = new CommandBean();
		commande1.name = "Commande de test 1";
		commande1.body = "Body de test 1\nBody de test 2\nBody de test 3\nBody de test 4\n";
		commande1.type = CommandType.CHACON;
		Mockito.when(mocked.doGetByIdBean("1")).thenReturn(commande1);
		CommandBean commande2 = new CommandBean();
		commande2.name = "Commande de test 2";
		commande2.body = "Body de test 1\nBody de test 2\nBody de test 3\nBody de test 4\n";
		commande2.type = CommandType.COMMAND;
		Mockito.when(mocked.doGetByIdBean("2")).thenReturn(commande2);
		return mocked;
	}

	/**
	 * @return ApiHrefPluginCommandResources
	 */
	@Bean
	public ApiHrefPluginCommandResources apiHrefPluginCommandResources() {
		ApiHrefPluginCommandResources mockedList = mock(ApiHrefPluginCommandResources.class);
		List<GenericEntity> entities = new ArrayList<GenericEntity>();
		GenericEntity e;
		e = new GenericEntity();
		e.id = "1";
		e.put("type", "data");
		e.put("name", "var1");
		entities.add(e );
		e = new GenericEntity();
		e.id = "2";
		e.put("type", "action");
		e.put("name", "var2");
		entities.add(e );
		Mockito.when(mockedList.findAll(any(ScriptPluginBean.class))).thenReturn(entities);
		return mockedList;
	}
}
