package org.jarvis.core.resources.api.tools;

import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.ResourcePair;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.ResultType;
import org.jarvis.core.type.TaskType;
import org.springframework.stereotype.Component;

/**
 * tools resources
 */
@Component
public class ApiCronResources extends ApiResources<CronRest,CronBean> {

	/**
	 * constructor
	 */
	public ApiCronResources() {
		setRestClass(CronRest.class);
		setBeanClass(CronBean.class);
	}

	@Override
	public void mount() {
		/**
		 * snapshot
		 */
		declare(CRON_RESOURCE);
	}

	@Override
	public ResourcePair doRealTask(CronBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			default:
				result = new GenericMap();
		}
		return new ResourcePair(ResultType.OBJECT, mapper.writeValueAsString(result));
	}
}
