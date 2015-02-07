/**
 * Copyright 2014 Yannick Roffin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jarvis.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.model.JarvisDatagram;
import org.jarvis.client.model.JarvisDatagramClient;
import org.jarvis.client.model.JarvisDatagramSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * default implementation of java protocol between nodejs bus and java client
 * 
 * @author yannick
 *
 */
public class JarvisSocketClientImpl implements IJarvisSocketClient {
	protected Logger logger = LoggerFactory
			.getLogger(JarvisSocketClientImpl.class);

	/**
	 * public member
	 */
	private String name;
	private boolean isRenderer;
	private boolean isSensor;
	private boolean canAnswer;

	/**
	 * internal member
	 */
	JarvisDatagramSession session = null;
	String hostName;
	int portNumber = 5000;
	Socket socket = null;
	private LinkedBlockingQueue<JarvisDatagram> linked = new LinkedBlockingQueue<JarvisDatagram>();
	private ObjectMapper mapper;
	private OutputStream output;

	/**
	 * constructor
	 * 
	 * @param hostname
	 */
	public JarvisSocketClientImpl(String hostName, int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
		mapper = new ObjectMapper();

		output = new OutputStream() {
			private StringBuilder string = new StringBuilder();

			@Override
			public void write(int b) throws IOException {
				this.string.append((char) b);
			}

			public String toString() {
				String result = this.string.toString();
				string.setLength(0);
				return result;
			}
		};
	}

	/**
	 * synchronize this client
	 * 
	 * @throws Exception
	 */
	public void sync() throws Exception {
		try {
			/**
			 * initialize socket
			 */
			socket = new Socket(hostName, portNumber);

			onConnect();

			/**
			 * start consume mecanism (thread based)
			 */
			Consumer consumer = new Consumer(socket.getInputStream());
			consumer.start();

			boolean identified = false;
			JarvisDatagram message = null;
			if (logger.isDebugEnabled()) {
				logger.error("Waiting for new message");
			}
			while ((message = linked.take()) != null && message.code != null) {
				if (!identified) {
					/**
					 * welcome message is the first element of this protocol
					 * client must answer on this welcome message and transmit
					 * all its properties
					 */
					if (message.getCode().startsWith("welcome")
							&& message.session != null
							&& message.session.client != null
							&& message.session.client.id != null) {
						/**
						 * fill element session session will be send with each
						 * message
						 */
						session = new JarvisDatagramSession();
						session.client = new JarvisDatagramClient();
						session.client.id = message.session.client.id;

						/**
						 * fill the session with parameters
						 */
						session.client.name = name;
						session.client.isRenderer = isRenderer;
						session.client.isSensor = isSensor;
						session.client.canAnswer = canAnswer;
					} else {
						logger.error("First message must be a welcome message, session must be filled");
					}
					/**
					 * resend this message (session element will be filled)
					 */
					sendMessage(message);
					identified = true;
				} else {
					/**
					 * default behaviour
					 */
					onNewMessage(message);
				}
			}
			consumer.join();
		} catch (Exception e) {
			logger.error("Error {} while syncing the system", e);
		}

		onDisconnect();
	}

	/**
	 * request handler
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void onNewRequestMessage(JarvisDatagram message) throws IOException {

	}

	/**
	 * message handler
	 * 
	 * @param message
	 * @throws IOException
	 */
	@Override
	public void onNewMessage(JarvisDatagram message) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.error("Message {}", message);
		}
		if (message.getCode().startsWith("welcome")) {
			JarvisDatagram nextMessage = new JarvisDatagram();
			sendMessage(nextMessage);
		}
		if (message.getCode().startsWith("request")) {
			JarvisDatagram nextMessage = new JarvisDatagram();
			onNewRequestMessage(nextMessage);
		}
	}

	@Override
	public void sendMessage(JarvisDatagram message) throws IOException {
		message.session = session;
		mapper.writeValue(output, message);
		if (logger.isDebugEnabled()) {
			logger.debug("Message {}", message);
		}
		socket.getOutputStream().write(output.toString().getBytes());
	}

	/**
	 * internal thread consumer implementation
	 * 
	 * @author yannick
	 *
	 */
	class Consumer extends Thread {
		private InputStream stream;

		public Consumer(InputStream stream) {
			this.stream = stream;
		}

		public void run() {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(stream));
			ObjectMapper mapper = new ObjectMapper();
			try {
				boolean cont = true;
				String line = null;
				while (cont && (line = in.readLine()) != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Line {}", line);
					}
					if (line.trim().length() == 0)
						continue;
					if (logger.isDebugEnabled()) {
						logger.debug("Line {}", line);
					}
					JarvisDatagram datagram = mapper.readValue(line,
							JarvisDatagram.class);
					try {
						if (logger.isDebugEnabled()) {
							logger.debug("Datagram {}", datagram);
						}
						linked.put(datagram);
					} catch (InterruptedException e) {
						logger.error(
								"InterruptedException while waiting for a new message {}",
								e);
						cont = false;
					}
				}
			} catch (IOException e) {
				logger.error("While waiting for a new message {}", e);
			}
			try {
				linked.put(new JarvisDatagram());
			} catch (InterruptedException e) {
				logger.error("While stopping {}", e);
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
		JarvisSocketClientImpl client = new JarvisSocketClientImpl("localhost",
				5000);
		client.sync();
		JarvisDatagram message = new JarvisDatagram();
		message.setCode("list");
		client.sendMessage(message);
		System.in.read();
	}

	/**
	 * accessor
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRenderer() {
		return isRenderer;
	}

	public void setRenderer(boolean isRenderer) {
		this.isRenderer = isRenderer;
	}

	public boolean isSensor() {
		return isSensor;
	}

	public void setSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}

	public void setCanAswer(boolean canAnswer) {
		this.canAnswer = canAnswer;
	}

	@Override
	public void onConnect() throws Exception {
	}

	@Override
	public void onDisconnect() throws Exception {
	}

	@Override
	public String toString() {
		return "JarvisSocketClientImpl [name=" + name + ", isRenderer="
				+ isRenderer + ", isSensor=" + isSensor + ", canAnswer="
				+ canAnswer + "]";
	}
}
