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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.jarvis.client.IJarvisSocketClient;
import org.jarvis.client.model.JarvisDatagram;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * default implementation of java protocol between nodejs bus and java client
 * @author yannick
 *
 */
public class JarvisSocketClientImpl implements IJarvisSocketClient {
	String hostName;
	int portNumber = 5000;
	Socket echoSocket = null;
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
		
		output = new OutputStream()
	    {
	        private StringBuilder string = new StringBuilder();

	        @Override
	        public void write(int b) throws IOException {
	            this.string.append((char) b );
	        }

	        public String toString(){
	            String result = this.string.toString();
	            string.setLength(0);
	            return result;
	        }
	    };
	}

	/**
	 * synchronize this client
	 */
	public void sync() {
		try {
			echoSocket = new Socket(hostName, portNumber);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
					true);
			Consumer consumer = new Consumer(echoSocket.getInputStream());
			consumer.start();

			JarvisDatagram message = null;
			while((message  = linked.take()) != null) {
				onNewMessage(message);
			}
		} catch (Exception e) {

		}
	}

	/**
	 * message handler
	 * @param message
	 * @throws IOException
	 */
	@Override
	public void onNewMessage(JarvisDatagram message) throws IOException {
		System.err.println("onNewMessage:"+message);		
		if(message.getCode().startsWith("welcome")) {
			JarvisDatagram nextMessage = new JarvisDatagram();
			nextMessage.setCode("list");
			sendMessage(nextMessage);
		}
	}

	@Override
	public void sendMessage(JarvisDatagram message) throws IOException {
		mapper.writeValue(output, message);
		getEchoSocket().getOutputStream().write(output.toString().getBytes());
	}

	/**
	 * internal thread consumer implementation
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
				String line = null;
				while((line = in.readLine()) != null) {
					System.err.println("msg:"+line);
					if(line.trim().length() == 0) continue;
					JarvisDatagram datagram = mapper.readValue(line, JarvisDatagram.class);
					try {
						linked.put(datagram);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * standard main procedure
	 * @param argv
	 * @throws IOException
	 */
	public static void main(String argv[]) throws IOException {
		JarvisSocketClientImpl client = new JarvisSocketClientImpl("localhost",
				5000);
		client.sync();
		JarvisDatagram message = new JarvisDatagram();
		message.setCode("list");
		client.sendMessage(message);
		System.in.read();
	}

	public Socket getEchoSocket() {
		return echoSocket;
	}
}
