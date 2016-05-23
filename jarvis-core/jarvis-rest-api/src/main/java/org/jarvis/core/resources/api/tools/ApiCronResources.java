package org.jarvis.core.resources.api.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourcePreListener;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;

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

	class ResourceListenerImpl implements ResourcePreListener<CronRest> {

		@Override
		public void post(Request request, Response response, CronRest rest) {
		}

		@Override
		public void put(Request request, Response response, CronRest rest) {
		}

		@Override
		public void get(Request request, Response response, CronRest rest) {
			/**
			 * post compute cron status and inject it
			 * in result
			 */
			rest.status = scheduled.containsKey(rest.id);
		}
	}

	@Override
	public void mount() {
		/**
		 * snapshot
		 */
		declare(CRON_RESOURCE);
		/**
		 * declare listener
		 */
		addListener(new ResourceListenerImpl());
	}

	@Override
	public GenericValue doRealTask(CronBean bean, GenericMap args, TaskType taskType) throws Exception {
		GenericMap result;
		switch(taskType) {
			case TOGGLE:
				return doToggle(bean);
			default:
				result = new GenericMap();
		}
		return new GenericValue(mapper.writeValueAsString(result));
	}

	/**
	 * @return ThreadPoolTaskScheduler
	 */
	@Bean
	public ThreadPoolTaskScheduler jarvisThreadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("jarvis-job-");
		scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		scheduler.setRemoveOnCancelPolicy(true);
		return scheduler;
	}
	
	@Autowired
	ThreadPoolTaskScheduler jarvisThreadPoolTaskScheduler;
	
	@Autowired
	CoreEventDaemon coreEventDaemon;

	/**
	 * internal cron registry
	 */
	ConcurrentMap<String,ScheduledFuture<?>> scheduled = new ConcurrentHashMap<String,ScheduledFuture<?>>();
	
	/**
	 * toggle cron
	 * @param bean
	 * @return GenericValue
	 */
	private synchronized GenericValue doToggle(CronBean bean) {
		if(scheduled.containsKey(bean.id)) {
			/**
			 * unschedule this cron
			 */
			ScheduledFuture<?> sch = scheduled.get(bean.id);
			sch.cancel(false);
			scheduled.remove(bean.id, sch);
			return new GenericValue("false");
		} else {
			/**
			 * schedule this cron
			 */
			CronTrigger trigger = new CronTrigger(bean.cron);
			ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
	
				@Override
				public void run() {
					EventBean event = new EventBean();
					event.text = "crontab-" + bean.id;
					coreEventDaemon.post(event);
				}
				
			}, trigger);
			
			scheduled.put(bean.id, sch);
			return new GenericValue("true");
		}
	}
}
