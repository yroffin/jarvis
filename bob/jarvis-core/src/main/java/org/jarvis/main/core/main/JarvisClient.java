package org.jarvis.main.core.main;

import java.io.IOException;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.impl.JarvisSocketClientImpl;
import org.jarvis.client.model.JarvisDatagram;

public class JarvisClient extends JarvisSocketClientImpl implements IJarvisSocketClient {

	/**
	 * constructor
	 * @param hostName
	 * @param portNumber
	 */
	public JarvisClient(String hostName, int portNumber) {
		super(hostName, portNumber);
		
		setName("Jarvis AIML Advisor");
		setRenderer(true);
		setSensor(true);
		setCanAswer(true);
	}

	@Override
	public void onNewMessage(JarvisDatagram message) throws IOException {
		if(message.getCode().startsWith("request")) {
			JarvisDatagram nextMessage = new JarvisDatagram();
			nextMessage.setCode("event");
			sendMessage(nextMessage);
		}
	}

	/**
	 * standard main procedure
	 * @param argv
	 * @throws IOException
	 */
	public static void main(String argv[]) throws IOException {
		JarvisSocketClientImpl client = new JarvisClient("localhost", 5000);
		client.sync();
		System.in.read();
	}
}
