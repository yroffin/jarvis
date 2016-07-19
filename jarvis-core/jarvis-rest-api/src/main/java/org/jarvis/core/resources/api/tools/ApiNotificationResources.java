package org.jarvis.core.resources.api.tools;

import java.lang.reflect.Field;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.tools.NotificationBean;
import org.jarvis.core.model.rest.tools.NotificationRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.services.slack.PluginSlackService;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * tools resources
 */
@Component
public class ApiNotificationResources extends ApiResources<NotificationRest,NotificationBean> {

	@Autowired
	PluginSlackService pluginSlackService;
	
	/**
	 * constructor
	 */
	public ApiNotificationResources() {
		setRestClass(NotificationRest.class);
		setBeanClass(NotificationBean.class);
	}

	@Override
	public void mount() {
		/**
		 * snapshot
		 */
		declare(NOTIFICATION_RESOURCE);
	}

	@Override
	public GenericValue doRealTask(NotificationBean bean, GenericMap args, TaskType taskType) throws TechnicalException {
		GenericMap result;
		switch(taskType) {
			case TEST:
				try {
					return doTest(bean, args);
				} catch (InterruptedException e) {
					logger.error("Error {}", e);
					throw new TechnicalException(e);
				}
			default:
				result = new GenericMap();
		}
		return new GenericValue(result);
	}

	/**
	 * execute notification
	 * @param bean
	 * @param args
	 * @return GenericValue
	 */
	public GenericValue doNotification(NotificationBean bean, GenericMap args) {
		GenericMap result = null;
		switch(bean.type) {
			case SLACK:
				result = pluginSlackService.asObject(extractNotification(bean), args);
			case MAIL:
				result = new GenericMap();
				break;
		}
		try {
			return new GenericValue(mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			logger.warn("Notification error {}", e);
		}
		return new GenericValue("{}");
	}
	
	/**
	 * transform NotificationBean to GenericMap
	 * @param bean
	 * @return
	 */
	private GenericMap extractNotification(NotificationBean bean) {
		/**
		 * convert command to lazy map
		 */
		GenericMap converted = new GenericMap();
		for(Field field : bean.getClass().getDeclaredFields()) {
			try {
				converted.put(field.getName(), field.get(bean));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new TechnicalException(e);
			}
		}
		return converted;
	}

	/**
	 * test cron
	 * @param bean
	 * @param args 
	 * @return GenericValue
	 * @throws InterruptedException 
	 */
	private GenericValue doTest(NotificationBean bean, GenericMap args) throws InterruptedException {
		return doNotification(bean, args);
	}
}
