package org.jarvis.core.resources.api.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourcePreListener;
import org.jarvis.core.resources.api.iot.ApiTriggerResources;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
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
			case TEST:
				return doTest(bean);
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
	
	public enum TRIGGER_TYPE {
		SUNSET, SUNRISE
	}
	
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
			Trigger trigger = null;
			if(bean.cron.equals(TRIGGER_TYPE.SUNRISE.name())) {
				/**
				 * create periodic sunrise event generator
				 */
			} else {
				if(bean.cron.equals(TRIGGER_TYPE.SUNSET.name())) {
					/**
					 * create periodic sunset event generator
					 */
				} else {
					/**
					 * crontab type
					 */
					trigger = new CronTrigger(bean.cron);
					ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
						
						@Override
						public void run() {
							fire(bean);
						}
						
					}, trigger);
					scheduled.put(bean.id, sch);
				}
			}
			
			return new GenericValue("true");
		}
	}

	/**
	 * test cron
	 * @param bean
	 * @return GenericValue
	 */
	private GenericValue doTest(CronBean bean) {
		fire(bean);
		return new GenericValue("{}");
	}

	@Autowired
	ApiTriggerResources apiTriggerResources;
	
	/**
	 * fire cron
	 * @param bean
	 */
	private void fire(CronBean bean) {
		for(TriggerBean trigger : apiTriggerResources.doFindAllBean()) {
			for(CronBean cron : doFindAllBean()) {
				if(cron.id.equals(bean.id)) {
					EventBean event = new EventBean();
					event.text = bean.name;
					event.trigger = trigger.id;
					coreEventDaemon.post(event);
				}
			}
		}
	}
}
