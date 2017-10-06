package org.jarvis.core.resources.api.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.common.core.exception.TechnicalException;
import org.common.core.exception.TechnicalNotFoundException;
import org.jarvis.core.model.bean.tools.CronBean;
import org.jarvis.core.model.bean.trigger.TriggerBean;
import org.jarvis.core.model.rest.GenericEntity;
import org.jarvis.core.model.rest.tools.CronRest;
import org.jarvis.core.resources.api.ApiResources;
import org.jarvis.core.resources.api.Declare;
import org.jarvis.core.resources.api.GenericValue;
import org.jarvis.core.resources.api.ResourceDefaultPostListenerImpl;
import org.jarvis.core.resources.api.ResourcePostListener;
import org.jarvis.core.resources.api.device.ApiTriggerResources;
import org.jarvis.core.resources.api.href.ApiHrefTriggerCronResources;
import org.jarvis.core.resources.api.mapper.ApiMapper;
import org.jarvis.core.services.CoreEventDaemon;
import org.jarvis.core.services.CoreSunsetSunrise;
import org.jarvis.core.services.CoreThreadPoolTaskScheduler;
import org.common.core.type.GenericMap;
import org.jarvis.core.type.TriggerType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import io.swagger.annotations.Api;
import spark.Request;
import spark.Response;

/**
 * tools resources
 */
@Component
@Api(value = "cron")
@Path("/api/crons")
@Produces("application/json")
@Declare(resource=ApiMapper.CRON_RESOURCE, summary="Cron resource", rest=CronRest.class)
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
	ConcurrentMap<String,AnalyzedRun> runners = new ConcurrentHashMap<>();
	
	/**
	 * constructor
	 */
	public ApiCronResources() {
		setRestClass(CronRest.class);
		setBeanClass(CronBean.class);
	}

	class ResourceListenerImpl extends ResourceDefaultPostListenerImpl<CronRest,CronBean> implements ResourcePostListener<CronRest,CronBean> {

		@Override
		public void getRest(Request request, Response response, CronRest rest) {
			/**
			 * post compute cron status and inject it
			 * in result
			 */
			rest.status = scheduled.containsKey(rest.id);
		}

	}

	@Override
	public void mount() {
		super.mount();
		/**
		 * declare listener
		 */
		addPostListener(new ResourceListenerImpl());
		/**
		 * start any cron at boot time
		 */
		for(CronBean cron : doFindAllBean()) {
			if(cron.startAtRuntime) {
				GenericMap args = new GenericMap();
				args.put("target", true);
				logger.warn("Toggle {}", cron);
				doToggle(cron, args);
			}
		}
	}

	@Override
	public GenericValue doRealTask(CronBean bean, GenericMap args, String taskType) throws TechnicalException {
		GenericMap result;
		switch(taskType) {
			case "toggle":
				return doToggle(bean, args);
			case "test":
				try {
					return doTest(bean);
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
	 * find target
	 * @param bean
	 * @param args
	 * @return boolean
	 */
	private boolean findTarget(CronBean bean, GenericMap args) {
		boolean target;
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
			AnalyzedRun runner = runners.get(bean.id);
			runners.remove(bean.id, runner);
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
				if(bean.triggerType.equalsIgnoreCase(TriggerType.SUNRISE.name())) {
					/**
					 * create periodic sunrise event generator
					 */
					sunriseTrigger(bean);
				} else {
					if(bean.triggerType.equalsIgnoreCase(TriggerType.SUNSET.name())) {
						/**
						 * create periodic sunset event generator
						 */
						sunsetTrigger(bean);
					} else {
						/**
						 * crontab
						 */
						if(bean.triggerType.equalsIgnoreCase(TriggerType.CRONTAB.name())) {
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

	private class TriggerThread implements AnalyzedRun {
		
		CronBean bean;
		DateTime lastRun = DateTime.now();
		
		public TriggerThread(CronBean bean) {
			this.bean = bean;
		}
		
		@Override
		public DateTime getLastRun() {
			return lastRun;
		}

		@Override
		public void run() {
			try {
				fire(bean);
				lastRun = DateTime.now();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

	/**
	 * crontab trigger
	 * @param bean
	 */
	private void crontabTrigger(CronBean bean) {
		Trigger trigger = new CronTrigger(bean.cron);
		AnalyzedRun analyzedRun = new TriggerThread(bean);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(analyzedRun, trigger);
		scheduled.put(bean.id, sch);
		runners.put(bean.id, analyzedRun);
	}

	private class SunriseTriggerThread implements AnalyzedRun {
		
		CronBean bean;
		DateTime lastRun = DateTime.now();
		
		public SunriseTriggerThread(CronBean bean) {
			this.bean = bean;
		}
		
		@Override
		public DateTime getLastRun() {
			return lastRun;
		}

		@Override
		public void run() {
			/**
			 * wait for next sunrise
			 */
			long millis = coreSunsetSunrise.getNextSunrise(bean.latitude, bean.longitude) + bean.shift * 60 * 1000;
			logger.warn("Sleep until next sunrise {}", DateTime.now().plus(millis));
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			try {
				fire(bean);
				lastRun = DateTime.now();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}
	
	/**
	 * sunrise trigger
	 * @param bean
	 */
	private void sunriseTrigger(CronBean bean) {
		Trigger trigger = new PeriodicTrigger(1000);
		AnalyzedRun analyzedRun = new SunriseTriggerThread(bean);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(analyzedRun, trigger);
		scheduled.put(bean.id, sch);
		runners.put(bean.id, analyzedRun);
	}

	private class SunsetTriggerThread implements AnalyzedRun {
		
		CronBean bean;
		DateTime lastRun = DateTime.now();
		
		public SunsetTriggerThread(CronBean bean) {
			this.bean = bean;
		}
		
		@Override
		public DateTime getLastRun() {
			return lastRun;
		}

		@Override
		public void run() {
			/**
			 * wait for next sunset
			 */
			long millis = coreSunsetSunrise.getNextSunset(bean.latitude, bean.longitude) + bean.shift * 60 * 1000;
			logger.warn("Sleep until next sunset {}", DateTime.now().plus(millis));
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				throw new TechnicalException(e);
			}
			try {
				fire(bean);
				lastRun = DateTime.now();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}		
	}

	/**
	 * sunrise trigger
	 * @param bean
	 */
	private void sunsetTrigger(CronBean bean) {
		Trigger trigger = new PeriodicTrigger(1000);
		AnalyzedRun analyzedRun = new SunsetTriggerThread(bean);
		ScheduledFuture<?> sch = jarvisThreadPoolTaskScheduler.schedule(analyzedRun, trigger);
		scheduled.put(bean.id, sch);
		runners.put(bean.id, analyzedRun);
	}

	/**
	 * test cron
	 * @param bean
	 * @return GenericValue
	 * @throws InterruptedException 
	 */
	private GenericValue doTest(CronBean bean) throws InterruptedException {
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
		boolean fired = false;
		for(TriggerBean trigger : apiTriggerResources.doFindAllBean()) {
			for(GenericEntity cron : apiHrefTriggerCronResources.findAll(trigger)) {
				if(cron.id.equals(bean.id)) {
					coreEventDaemon.post(trigger.id, bean.name);
				}
			}
		}
		if(!fired) {
			logger.warn("[EVENT] no trigger for {}", bean);
		}
	}

	/**
	 * scheduled accessor
	 * @return ConcurrentMap<String, ScheduledFuture<?>>
	 */
	public List<CronRest> getScheduled() {
		List<CronRest> sched = new ArrayList<CronRest>();
		for(String id: scheduled.keySet()) {
			try {
				CronRest obj = doGetByIdRest(id);
				obj.status = scheduled.containsKey(obj.id);
				if(obj.status) {
					obj.lastExecution = runners.get(obj.id).getLastRun();
				}
				sched.add(obj);
			} catch (TechnicalNotFoundException e) {
				throw new TechnicalException(e);
			}
		}
		return sched;
	}
}
