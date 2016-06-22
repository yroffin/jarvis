package org.jarvis.core.resources.api.tools;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import org.jarvis.core.exception.TechnicalException;
import org.jarvis.core.model.bean.iot.EventBean;
import org.jarvis.core.model.bean.scenario.TriggerBean;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourcePreListener;
import org.jarvis.core.resources.api.href.ApiHrefTriggerCronResources;
import org.jarvis.core.resources.api.iot.ApiTriggerResources;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.services.CoreSunsetSunrise;
import org.jarvis.core.services.CoreThreadPoolTaskScheduler;
import org.jarvis.core.type.GenericMap;
import org.jarvis.core.type.TaskType;
import org.jarvis.core.type.TriggerType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import spark.Request;
import spark.Response;

/**
 * tools resources
 */
@Component
public class ApiCronResources extends ApiResources<CronRest,CronBean> {

	@Autowired
	CoreThreadPoolTaskScheduler jarvisThreadPoolTaskScheduler;
	
	@Autowired
	CoreEventDaemon coreEventDaemon;

	@Autowired
	CoreSunsetSunrise coreSunsetSunrise;
	
	/**
	 * internal cron registry
	 */
	ConcurrentMap<String,ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();
	
	/**
	 * constructor
	 */
	public ApiCronResources() {
		setRestClass(CronRest.class);
		setBeanClass(CronBean.class);
	}

	class ResourceListenerImpl extends ResourcePreListener<CronRest> {

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
				return doToggle(bean, args);
			case TEST:
				return doTest(bean, args);
			default:
				result = new GenericMap();
		}
		return new GenericValue(mapper.writeValueAsString(result));
	}

	/**
	 * find target
	 * @param bean
	 * @param args
	 * @return boolean
	 */
	private boolean findTarget(CronBean bean, GenericMap args) {
		boolean target = !scheduled.containsKey(bean.id);
		/**
		 * if target is null compute it with crontab status
		 */
		if(args.get("target") == null) {
			target = !scheduled.containsKey(bean.id);
		} else {
			target = args.get("target").equals(true);
		}
		return target;
	}

	/**
	 * compute result
	 * @param bean
	 * @param target
	 * @return GenericValue
	 */
	private GenericValue compute(CronBean bean, boolean target) {
		if(target == false) {
			/**
			 * unschedule this cron
			 */
			ScheduledFuture<?> sch = scheduled.get(bean.id);
			sch.cancel(false);
			scheduled.remove(bean.id, sch);
			return new GenericValue("false");
		} else {
			return new GenericValue("true");
		}
	}

	/**
	 * toggle cron
	 * @param bean
	 * @param args 
	 * @return GenericValue
	 */
	private synchronized GenericValue doToggle(CronBean bean, GenericMap args) {
		/**
		 * find target
		 */
		boolean target = findTarget(bean, args);
		
		if(scheduled.containsKey(bean.id)) {
			return compute(bean, target);
		} else {
			if(target == true) {
				if(bean.triggerType.toUpperCase().equals(TriggerType.SUNRISE.name())) {
					/**
					 * create periodic sunrise event generator
					 */
					sunriseTrigger(bean);
				} else {
					if(bean.triggerType.toUpperCase().equals(TriggerType.SUNSET.name())) {
						/**
						 * create periodic sunset event generator
						 */
						sunsetTrigger(bean);
					} else {
						/**
						 * crontab
						 */
						if(bean.triggerType.toUpperCase().equals(TriggerType.CRONTAB.name())) {
							/**
							 * crontab type
							 */
							crontabTrigger(bean);
						} else {
							logger.warn("No existing trigger type {}", bean.triggerType);
						}
					}
				}	
				return new GenericValue("true");
			} else {
				return new GenericValue("false");
			}
		}
	}

	/**
	 * crontab trigger
	 * @param bean
	 */
	private void crontabTrigger(CronBean bean) {
		Trigger trigger = new CronTrigger(bean.cron);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				try {
					fire(bean);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
		}, trigger);
		scheduled.put(bean.id, sch);
	}

	/**
	 * sunrise trigger
	 * @param bean
	 */
	private void sunriseTrigger(CronBean bean) {
		Trigger trigger = new PeriodicTrigger(1000);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				/**
				 * wait for next sunrise
				 */
				long millis = coreSunsetSunrise.getNextSunrise(bean.latitude, bean.longitude) + 60000;
				logger.warn("Sleep until next sunrise {}", DateTime.now().plus(millis));
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					throw new TechnicalException(e);
				}
				try {
					fire(bean);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
		}, trigger);
		scheduled.put(bean.id, sch);
	}

	/**
	 * sunrise trigger
	 * @param bean
	 */
	private void sunsetTrigger(CronBean bean) {
		Trigger trigger = new PeriodicTrigger(1000);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(new Runnable() {
			
			@Override
			public void run() {
				/**
				 * wait for next sunset
				 */
				long millis = coreSunsetSunrise.getNextSunset(bean.latitude, bean.longitude) + 60000;
				logger.warn("Sleep until next sunset {}", DateTime.now().plus(millis));
				try {
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					throw new TechnicalException(e);
				}
				try {
					fire(bean);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
		}, trigger);
		scheduled.put(bean.id, sch);
	}

	/**
	 * test cron
	 * @param bean
	 * @return GenericValue
	 * @throws InterruptedException 
	 */
	private GenericValue doTest(CronBean bean, GenericMap args) throws InterruptedException {
		fire(bean);
		return new GenericValue("{}");
	}

	@Autowired
	ApiTriggerResources apiTriggerResources;
	
	@Autowired
	ApiHrefTriggerCronResources apiHrefTriggerCronResources;
	
	/**
	 * fire cron
	 * @param bean
	 * @throws InterruptedException 
	 */
	private void fire(CronBean bean) throws InterruptedException {
		for(TriggerBean trigger : apiTriggerResources.doFindAllBean()) {
			for(GenericEntity cron : apiHrefTriggerCronResources.findAll(trigger)) {
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
