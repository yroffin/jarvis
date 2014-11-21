package org.jarvis.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.jarvis.client.IJarvisSocketClient;

public class JarvisSocketClientImpl implements IJarvisSocketClient {
	String hostName;
	int portNumber = 5000;
	Socket echoSocket = null;

	public Socket getEchoSocket() {
		return echoSocket;
	}

	/**
	 * constructor
	 * 
	 * @param hostname
	 */
	public JarvisSocketClientImpl(String hostName, int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
	}

	void sync() {
		try {
			echoSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					echoSocket.getInputStream()));
		} catch (Exception e) {

		}
	}
	
	public static void main(String argv[]) throws IOException {
		JarvisSocketClientImpl client = new JarvisSocketClientImpl("localhost", 5000);
		client.sync();
		client.getEchoSocket().getOutputStream().write("test".getBytes());;
	}
}
