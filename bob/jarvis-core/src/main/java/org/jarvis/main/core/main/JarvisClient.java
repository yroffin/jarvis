package org.jarvis.main.core.main;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.impl.JarvisSocketClientImpl;
import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramEvent;
import org.jarvis.main.core.IJarvisCoreSystem;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.main.core.impl.JarvisCoreSystemImpl;
import org.jarvis.main.model.parser.history.IAimlHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarvisClient extends JarvisSocketClientImpl implements
		IJarvisSocketClient {

	protected Logger logger = LoggerFactory
			.getLogger(JarvisClient.class);
	
	IJarvisCoreSystem jarvis;

	/**
	 * constructor
	 * 
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisClient(String hostName, int portNumber) {
		super(hostName, portNumber);

		setName("Jarvis AIML Advisor");
		setRenderer(true);
		setSensor(true);
		setCanAswer(true);
		
		/**
		 * implement jarvis system
		 */
		jarvis = new JarvisCoreSystemImpl();
	}

	@Override
	public void onConnect() throws Exception {
		initialize();
	}

	@Override
	public void onDisconnect() throws Exception {
	}

	private void initialize() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, AimlParsingError,
			IOException {
		System.setProperty("file.encoding", "UTF-8");
		java.lang.reflect.Field charset = Charset.class
				.getDeclaredField("defaultCharset");
		charset.setAccessible(true);
		charset.set(null, null);
		logger.info("Default encoding: {}", Charset.defaultCharset().displayName());
		logger.info("Initializing ...");
		jarvis.initialize("categories.txt");
		logger.info("Ready ...");
	}

	@Override
	public void onNewMessage(JarvisDatagram message) throws IOException {
		if (message.getCode().startsWith("request")) {
			try {
				List<IAimlHistory> result = jarvis.ask(message.request.getData());
				for (IAimlHistory value : result) {
					/**
					 * on event per answer
					 */
					JarvisDatagram nextMessage = new JarvisDatagram();
					nextMessage.setCode("event");
					nextMessage.event = new JarvisDatagramEvent();
					nextMessage.event.setData(value.getAnswer());
					sendMessage(nextMessage);
				}
			} catch (AimlParsingError e) {
				logger.error("Error, while accessing to jarvis with {}", message.request.getData());
				return;
			}
		}
	}

	/**
	 * standard main procedure
	 * 
	 * @param argv
	 * @throws Exception 
	 */
	public static void main(String argv[]) throws Exception {
		JarvisSocketClientImpl client = new JarvisClient("localhost", 5000);
		client.sync();
		System.in.read();
	}
}
