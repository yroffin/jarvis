package org.jarvis.core.services;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * core task thread
 */
@Component
public class CoreThreadPoolTaskScheduler extends ThreadPoolTaskScheduler {

	protected Logger logger = LoggerFactory.getLogger(CoreThreadPoolTaskScheduler.class);

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 7750643698080731384L;

	/**
	 * core task thread
	 */
	public CoreThreadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("jarvis-job-");
		scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() + 1);
		logger.warn("Pool size: {}", scheduler.getPoolSize());
		scheduler.setRejectedExecutionHandler(new RejectedExecutionHandler() {

			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				logger.warn("Pool is exhausted, but run thread anyway");
				executor.execute(r);				
			}
			
		});
		scheduler.setRemoveOnCancelPolicy(true);
	}
}
