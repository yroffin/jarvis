package org.jarvis.rest;

import org.jarvis.rest.services.CoreRestDaemon;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * simple bootstrap for rest bootstrap
 */
public class bootstrap {
	private static final String CONFIG_PATH = "classpath*:org/jarvis/application-config.xml";

	/**
	 * main entry
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);

		final CoreRestDaemon daemon = context.getBean(CoreRestDaemon.class);
		daemon.server();
		((AbstractApplicationContext) context).close();
	}
}
