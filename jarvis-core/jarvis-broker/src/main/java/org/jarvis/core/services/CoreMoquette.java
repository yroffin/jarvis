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

package org.jarvis.core.services;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.InterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.moquette.server.config.ClasspathResourceLoader;
import io.moquette.server.config.IConfig;
import io.moquette.server.config.IResourceLoader;
import io.moquette.server.config.ResourceLoaderConfig;
import static java.util.Arrays.asList;

/**
 * main daemon
 */
@Component
public class CoreMoquette {
	protected Logger logger = LoggerFactory.getLogger(CoreMoquette.class);

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	Environment env;

	/**
	 * start component
	 */
	@PostConstruct
	public void server() {
		try {
			CoreMoquette.start();
		} catch (InterruptedException | IOException e) {
			logger.error("While starting moquette {}", e);
		}
	}

	/**
	 * internal listener
	 */
	static class PublisherListener extends AbstractInterceptHandler {
		protected Logger logger = LoggerFactory.getLogger(PublisherListener.class);

		@Override
		public void onPublish(InterceptPublishMessage msg) {
			System.out.println(
					"Received on topic: " + msg.getTopicName() + " content: " + new String(msg.getPayload().array()));
		}
	}

	/**
	 * start moquette
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void start() throws InterruptedException, IOException {
        IResourceLoader classpathLoader = new ClasspathResourceLoader("moquette.conf");
        final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader);

        final Server mqttBroker = new Server();
        List<? extends InterceptHandler> userHandlers = asList(new PublisherListener());
        mqttBroker.startServer(classPathConfig, userHandlers);

        /**
         * hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Stopping broker");
                mqttBroker.stopServer();
                System.out.println("Broker stopped");
            }
        });
        }
	}
