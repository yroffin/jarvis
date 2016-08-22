package org.jarvis.core.services;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * core task thread
 */
@Component
public class ModuleThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 7750643698080731354L;

	/**
	 * core task thread
	 */
	public ModuleThreadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("jarvis-module-");
		scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		scheduler.setRemoveOnCancelPolicy(true);
	}
}
