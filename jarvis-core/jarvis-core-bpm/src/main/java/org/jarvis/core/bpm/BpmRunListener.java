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

package org.jarvis.core.bpm;

import static java.util.Objects.requireNonNull;
import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Groups.CAMUNDA_ADMIN;
import static org.camunda.bpm.engine.authorization.Permissions.ALL;

import java.util.Optional;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.authorization.Groups;
import org.camunda.bpm.engine.authorization.Resource;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.persistence.entity.AuthorizationEntity;
import org.camunda.bpm.spring.boot.starter.property.AdminUserProperty;
import org.jarvis.core.bpm.execution.BpmPluginBehaviour;
import org.jarvis.core.bpm.execution.BpmServiceTaskBehaviour;
import org.jarvis.core.bpm.listener.BpmListener;
import org.jarvis.core.model.bean.process.ProcessBean;
import org.jarvis.core.resources.api.process.ApiProcessResources;
import org.jarvis.core.services.CoreMoquette;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * BpmRunListener
 */
public class BpmRunListener implements SpringApplicationRunListener {
	protected Logger logger = LoggerFactory.getLogger(BpmRunListener.class);

	private static ConfigurableApplicationContext context;

	/**
	 * get context
	 * @return ApplicationContext
	 */
	public static ApplicationContext getContext() {
		return BpmRunListener.context;
	}

	/**
	 * @param application
	 * @param args
	 */
	public BpmRunListener(SpringApplication application, String[] args) {
	}

	@Override
	public void starting() {
	}

	@Override
	public void environmentPrepared(ConfigurableEnvironment environment) {
	}

	@Override
	public void contextPrepared(ConfigurableApplicationContext context) {
	}

	@Override
	public void contextLoaded(ConfigurableApplicationContext context) {
		BpmRunListener.context = context;
		BpmPluginBehaviour.setContext(context);
		BpmServiceTaskBehaviour.setContext(context);
	}

	@Override
	public void finished(ConfigurableApplicationContext context, Throwable exception) {
		if (exception != null) {
			return;
		}

		BpmListener.coreMoquette = context.getBean(CoreMoquette.class);

		/**
		 * setup
		 */
		postProcessEngineBuild(context);

		RuntimeService runtimeService;
		runtimeService = context.getBean(RuntimeService.class);

		/**
		 * start main process
		 */
		logger.info("Spring Application configuration finished");
		runtimeService.startProcessInstanceByKey("ProcessJarvis");

		/**
		 * load custom process
		 */
		ApiProcessResources apiProcessResources;
		apiProcessResources = context.getBean(ApiProcessResources.class);
		for (ProcessBean bean : apiProcessResources.doFindAllBean()) {
			try {
				apiProcessResources.deploy(bean);
			} catch (Exception e) {
				logger.warn("While deploying custom process {} {}", bean.name, e);
			}
		}
	}

	/**
	 * load admin setup
	 * @param context
	 */
	public void postProcessEngineBuild(ConfigurableApplicationContext context) {
		Optional<AdminUserProperty> adminUser = Optional.ofNullable(new AdminUserProperty());
		adminUser.get().setId("admin");
		adminUser.get().setPassword("admin");
		adminUser.get().setFirstName("Admin");
		adminUser.get().setLastName("Admin");
		adminUser.get().setEmail("admin@admin.org");

		requireNonNull(adminUser);

		final IdentityService identityService = context.getBean(IdentityService.class);
		final AuthorizationService authorizationService = context.getBean(AuthorizationService.class);

		if (userAlreadyExists(identityService, adminUser.get())) {
			return;
		}

		createUser(identityService, adminUser.get());

		// create group
		if (identityService.createGroupQuery().groupId(CAMUNDA_ADMIN).count() == 0) {
			Group camundaAdminGroup = identityService.newGroup(CAMUNDA_ADMIN);
			camundaAdminGroup.setName("camunda BPM Administrators");
			camundaAdminGroup.setType(Groups.GROUP_TYPE_SYSTEM);
			identityService.saveGroup(camundaAdminGroup);
		}

		// create ADMIN authorizations on all built-in resources
		for (Resource resource : Resources.values()) {
			if (authorizationService.createAuthorizationQuery().groupIdIn(CAMUNDA_ADMIN).resourceType(resource)
					.resourceId(ANY).count() == 0) {
				AuthorizationEntity userAdminAuth = new AuthorizationEntity(AUTH_TYPE_GRANT);
				userAdminAuth.setGroupId(CAMUNDA_ADMIN);
				userAdminAuth.setResource(resource);
				userAdminAuth.setResourceId(ANY);
				userAdminAuth.addPermission(ALL);
				authorizationService.saveAuthorization(userAdminAuth);
			}
		}

		identityService.createMembership(adminUser.get().getId(), CAMUNDA_ADMIN);
		logger.warn("Admin user {} created", adminUser.get());
	}

	static boolean userAlreadyExists(IdentityService identityService, User adminUser) {
		final User existingUser = identityService.createUserQuery().userId(adminUser.getId()).singleResult();
		if (existingUser != null) {
			return true;
		}
		return false;
	}

	static User createUser(final IdentityService identityService, final User adminUser) {
		User newUser = identityService.newUser(adminUser.getId());
		BeanUtils.copyProperties(adminUser, newUser);
		identityService.saveUser(newUser);
		return newUser;
	}
}
