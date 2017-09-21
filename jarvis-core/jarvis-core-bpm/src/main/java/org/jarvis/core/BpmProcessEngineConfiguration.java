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
		SpringProcessApplication processApplication = new SpringProcessApplication();
		return processApplication;
	}
}
