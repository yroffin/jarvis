package org.jarvis.main.core.main;

import java.io.IOException;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.impl.JarvisSocketClientImpl;
import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramEvent;
import org.jarvis.main.core.JarvisDatagramExec;
import org.jarvis.runtime.ProcessExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JarvisRemoteExec extends JarvisSocketClientImpl implements
		IJarvisSocketClient {

	protected Logger logger = LoggerFactory.getLogger(JarvisRemoteExec.class);

	public JarvisRemoteExec(String name, String hostName, int portNumber) {
		super(name, hostName, portNumber);

		setRenderer(true);
		setSensor(true);
		setCanAswer(true);

		mapper = new ObjectMapper();
	}

	private ObjectMapper mapper;

	@Override
	public void onNewMessage(JarvisDatagram message) throws IOException {
		try {
			String result = ProcessExec.execute(message.request.getData());
			JarvisDatagramExec resultConsole = mapper.readValue(result,
					JarvisDatagramExec.class);
			JarvisDatagram nextMessage = new JarvisDatagram();
			nextMessage.setCode("event");
			nextMessage.event = new JarvisDatagramEvent();
			nextMessage.event.setData("Result:" + resultConsole.result);
			nextMessage.event.setScript(result);
			sendMessage(nextMessage);
		} catch (Exception e) {
			logger.error(
					"Error, while accessing to jarvis with {} exception {}",
					message.request.getData(), e.getMessage());
			return;
		}
	}
}
