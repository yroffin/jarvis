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

package org.jarvis.core;

import javax.sql.DataSource;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.spring.ProcessEngineFactoryBean;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.engine.spring.application.SpringProcessApplication;
import org.camunda.bpm.engine.spring.container.ManagedProcessEngineFactoryBean;
import org.camunda.bpm.extension.reactor.CamundaReactor;
import org.camunda.bpm.extension.reactor.bus.CamundaEventBus;
import org.jarvis.core.bpm.BpmGenericExecutionListener;
import org.jarvis.core.bpm.listener.BpmServiceTaskEndExecutionListener;
import org.jarvis.core.bpm.listener.BpmServiceTaskStartExecutionListener;
import org.jarvis.core.bpm.listener.BpmStartEventEndExecutionListener;
import org.jarvis.core.bpm.listener.BpmStartEventStartExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.vendor.OpenJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Camunda configuration
 */
@Configuration
@EnableAutoConfiguration 
@ComponentScan
public class BpmProcessEngineConfiguration {
	protected Logger logger = LoggerFactory.getLogger(BpmProcessEngineConfiguration.class);

	/**
	 * @return PersistenceExceptionTranslator
	 */
	@Bean
	public PersistenceExceptionTranslator persistenceExceptionTranslator() {
		return new OpenJpaDialect();
	}

	/**
	 * @return EmbeddedServletContainerCustomizer
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return (container -> {
			container.setPort(8181);
		});
	}

	/**
	 * @return DataSource
	 */
	@Bean
	public DataSource dataSource() {
		/**
		 * Note: The following shows only a simple data source for In-Memory H2
		 * database. Simple H2 in memory database in sufficient for jarvis In
		 * our case
		 */

		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(org.h2.Driver.class);
		dataSource.setUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	/**
	 * @return PlatformTransactionManager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	/**
	 * @return SpringProcessEngineConfiguration
	 */
	@Bean
	public SpringProcessEngineConfiguration processEngineConfiguration() {
		SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

		config.setDataSource(dataSource());
		config.setTransactionManager(transactionManager());

		config.setDatabaseSchemaUpdate("true");
		config.setHistory("audit");
		config.setJobExecutorActivate(true);
		
		config.getProcessEnginePlugins().add(CamundaReactor.plugin());
		
		return config;
	}

	/**
	 * @return ProcessEngineFactoryBean
	 */
	@Bean
	public ProcessEngineFactoryBean processEngine() {
		ManagedProcessEngineFactoryBean factoryBean = new ManagedProcessEngineFactoryBean();
		factoryBean.setProcessEngineConfiguration(processEngineConfiguration());
		return factoryBean;
	}

	/**
	 * @param processEngine
	 * @return RepositoryService
	 */
	@Bean
	public RepositoryService repositoryService(ProcessEngine processEngine) {
		return processEngine.getRepositoryService();
	}

	/**
	 * @param processEngine
	 * @return RuntimeService
	 */
	@Bean
	public RuntimeService runtimeService(ProcessEngine processEngine) {
		return processEngine.getRuntimeService();
	}

	/**
	 * @param processEngine
	 * @return TaskService
	 */
	@Bean
	public TaskService taskService(ProcessEngine processEngine) {
		return processEngine.getTaskService();
	}

	/**
	 * @param processEngine
	 * @return SpringProcessApplication
	 */
	@Bean
	public SpringProcessApplication springProcessApplication(ProcessEngine processEngine) {
		/**
		 * register all listener
		 */
		CamundaEventBus eventBus = CamundaReactor.eventBus();
		eventBus.register(new BpmGenericExecutionListener());
		eventBus.register(new BpmServiceTaskStartExecutionListener());
		eventBus.register(new BpmServiceTaskEndExecutionListener());
		eventBus.register(new BpmStartEventStartExecutionListener());
		eventBus.register(new BpmStartEventEndExecutionListener());

		SpringProcessApplication processApplication = new SpringProcessApplication();
	    return processApplication;
	}
}
