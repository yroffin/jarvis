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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * main daemon
 */
@Component
public class CoreRflink {
	protected Logger logger = LoggerFactory.getLogger(CoreRflink.class);

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	CoreMoquette coreMoquette;

	@Autowired
	Environment env;

	/**
	 * start component
	 */
	@PostConstruct
	public void server() {
		start();
	}

	/**
	 * start rflink handler
	 */
	public void start() {
		String[] portNames = SerialPortList.getPortNames();
		for (int i = 0; i < portNames.length; i++) {
			logger.warn("Checking COM port {}", portNames[i]);
			if (env.getProperty("jarvis.rflink.comport") != null
					&& env.getProperty("jarvis.rflink.comport").equals(portNames[i])) {
				logger.warn("COM {} bind RFLINK driver", portNames[i]);
				startRflink(portNames[i]);
			}
		}
	}

	/*
	 * In this class must implement the method serialEvent, through it we learn
	 * about events that happened to our port. But we will not report on all
	 * events but only those that we put in the mask. In this case the arrival
	 * of the data and change the status lines CTS and DSR
	 */
	static SerialPort serialPort;
	static private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(16);

	static class SerialPortReader implements SerialPortEventListener {
		protected Logger logger = LoggerFactory.getLogger(SerialPortReader.class);

		/**
		 * handler
		 */
		public void serialEvent(SerialPortEvent event) {
			logger.trace("PORT {} TYPE {} VALUE {}", event.getPortName(), event.getEventType(), event.getEventValue());
			if (event.isRXCHAR()) {// If data is available
				try {
					byte buffer[] = serialPort.readBytes(event.getEventValue());
					try {
						queue.put(new String(buffer));
					} catch (InterruptedException e) {
						logger.error("Queue {}", e);
						Thread.currentThread().interrupt();
					}
				} catch (SerialPortException ex) {
					logger.error("SerialPortException {}", ex);
				}
			} else if (event.isCTS()) {// If CTS line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					logger.trace("CTS - ON");
				} else {
					logger.trace("CTS - OFF");
				}
			} else if (event.isDSR()) {/// If DSR line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					logger.trace("DSR - ON");
				} else {
					logger.trace("DSR - OFF");
				}
			}
		}

	}

	static class Consumer implements Runnable {
		protected Logger logger = LoggerFactory.getLogger(SerialPortReader.class);
		private final BlockingQueue<String> queue;
		private final CoreMoquette broker;
		
		StringBuffer sb = new StringBuffer();

		Consumer(CoreMoquette cm, BlockingQueue<String> q) {
			queue = q;
			broker = cm;
		}

		public void run() {
			try {
				while (true) {
					consume(queue.take());
				}
			} catch (InterruptedException ex) {
				logger.error("Consumer {}", ex);
				Thread.currentThread().interrupt();
			}
		}

		void consume(String x) {
			String normalized = x.replace("\r", "");
			for(int i = 0; i < normalized.length(); i++) {
				switch(normalized.charAt(i)) {
					case '\n':
						flush();
						break;
					default:
						store(normalized.charAt(i));
						break;
				}
			}
		}
		
		void flush() {
			logger.trace("FLUSH {}", sb.toString());
			broker.publishExactlyOnce("rflink", sb.toString());
			sb.setLength(0);
		}
		
		void store(char v) {
			sb.append(v);
		}
	}

	/**
	 * RfLink driver
	 * @param com
	 */
	private void startRflink(String com) {
		serialPort = new SerialPort(com);
		try {
			serialPort.openPort();// Open port
			serialPort.setParams(SerialPort.BAUDRATE_57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);// Set params
			int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;// Prepare
																							// mask
			serialPort.setEventsMask(mask);// Set mask
			serialPort.addEventListener(new SerialPortReader());// Add
																// SerialPortEventListener
		} catch (SerialPortException ex) {
			logger.error("SerialPortException {}", ex);
		}
		/**
		 * async processing
		 */
		new Thread(new Consumer(coreMoquette, queue)).start();
	}

	/**
	 * write chacon command
	 * @param id
	 * @param sw
	 * @param command
	 * @return String
	 */
	public String chacon(String id, String sw, String command) {
		try {
			String cmd = "10;NewKaku;" + id + ";" + sw + ";" + command + ";";
			if(serialPort.writeBytes((cmd + "\r\n").getBytes())) {
				coreMoquette.publishMostOne("rflink", cmd);
				return cmd + " Ok";
			} else {
				return cmd + " Ko ...";
			}
		} catch (SerialPortException e) {
			logger.error("SerialPortException {}", e);
			return e.getMessage();
		}
	}
}