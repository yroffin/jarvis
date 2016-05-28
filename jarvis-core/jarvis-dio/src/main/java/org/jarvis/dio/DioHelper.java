/**
 *  Copyright 2015 Yannick Roffin
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
package org.jarvis.dio;

import java.io.IOException;

import org.jarvis.runtime.ProcessExec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.pi4j.wiringpi.Gpio;

/**
 * DioHelper
 * Cf. http://pi4j.com/example/control.html
 */
public class DioHelper {

	/**
	 * logger
	 */
	protected static Logger logger = LoggerFactory.getLogger(DioHelper.class);
	
	private int pin = -1;
	
	/**
	 * setter
	 * @param pin
	 */
	public void setPin(int pin) {
		if(this.pin == pin) return;
		this.pin = pin;
		logger.info("Select pin {}", pin);
	}


	private int sender;
	private int interruptor;
	private int bit2sender[] = new int[26];     // 26 bit sender identifier
	private int bit2Interruptor[] = new int[4]; // 4 bit interuptor
	
	/**
	 * default constructor
	 */
	public DioHelper() {
	}
	
	/**
	 * http://www.slf4j.org/api/org/slf4j/bridge/SLF4JBridgeHandler.html
	 */
	{
		// Optionally remove existing handlers attached to j.u.l root logger
		 SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)

		 // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
		 // the initialization phase of your application
		 SLF4JBridgeHandler.install();
	}
	
	static boolean simulation = true;
	
	boolean wiringPiSetup() {
		try {
			Gpio.wiringPiSetup();
		} catch(Throwable e) {
			return false;
		}
		return true;
	}

	/**
	 * activate switch
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void switchOnProcess() throws IOException, InterruptedException {
		logger.info("SwitchOn: pin={} sender={} interruptor={} status={}", pin, sender, interruptor, "on");
		ProcessExec.execute("sudo raspi-dio "+pin+" "+sender+" "+interruptor+" on");
	}

	/**
	 * activate switch
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void switchOn() throws InterruptedException, IOException {
		if(!simulation) {
			for(int i=0;i<5;i++) {
				transmit(1);
				Gpio.piHiPri(0);
				delay(10);
			}
		} else {
			switchOnProcess();
		}
	}

	/**
	 * un-activate switch
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void switchOff() throws InterruptedException, IOException {
		if(!simulation) {
			for(int i=0;i<5;i++) {
				transmit(0);
				Gpio.piHiPri(0);
				delay(10);
			}
		} else {
			switchOffProcess();
		}
	}

	/**
	 * activate switch
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public void switchOffProcess() throws IOException, InterruptedException {
		logger.info("SwitchOn: pin={} sender={} interruptor={} status={}", pin, sender, interruptor, "off");
		ProcessExec.execute("sudo raspi-dio "+pin+" "+sender+" "+interruptor+" off");
	}

	/**
	 * wait on micro second
	 * @param nanos
	 */
	private static void delayMicroseconds(long nanos) {
		try {
			Gpio.delayMicroseconds(nanos);
		} catch(Throwable e) {
			/**
			 * catch this exception in order to
			 * test this code on pc plateform, delayMicroseconds only existe
			 * in wiringPi library
			 */
		}
	}
	
	/**
	 * wait on milliseconds second
	 * @param nanos
	 */
	private static void delay(long milliseconds) {
		try {
			Gpio.delay(milliseconds);
		} catch(Throwable e) {
			/**
			 * catch this exception in order to
			 * test this code on pc plateform, delayMicroseconds only existe
			 * in wiringPi library
			 */
		}
	}
	
	/**
	 * transmit value
	 * @param value
	 */
	private void transmit(int value) {
		int i;

//		logger.info("Lock Sequence announcing the starting signal to the receiver on pin {}", pin);
//		logger.info("Transmitter sends the code {}", bit2sender);
//		logger.info("Sends the bit defining whether it is a control group or not (26th bit) {}", 0);
//		logger.info("Sends the bit defining whether it is on or extinguished 27th bit) {}", value);
//		logger.info("Sends the last 4 bits {}", bit2Interruptor);
		
		// Lock Sequence announcing the starting signal to the receiver
		digitalWrite(pin, 1);
		delayMicroseconds(275);  // a noise bit before starting to put the delays of the receiver 0
		digitalWrite(pin, 0);
		delayMicroseconds(9900); // 9900μs first lock
		digitalWrite(pin, 1);    // high again
		delayMicroseconds(275);  // pending 275μs between two locks
		digitalWrite(pin, 0);    // 2675μs second lock
		delayMicroseconds(2675);
		digitalWrite(pin, 1);    // Returning to high to cut the locks of well data

		// Transmitter sends the code (272946 = 1000010101000110010 binary)
		for (i = 0; i < 26; i++) {
			sendPair(bit2sender[i]);
		}

		// Sends the bit defining whether it is a control group or not (26th bit)
		sendPair(0);

		// Sends the bit defining whether it is on or extinguished 27th bit)
		sendPair(value);

		// Sends the last 4 bits, representing the switch code here 0 (4 encodes bit so 0000)
		// note: on official chacon remote controls, switches are logically named from 0 to x
		// switch 1 = 0 (ie 0000), switch 2 = 1 (1000), switch 3 = 2 (0100), etc ...
		for (i = 0; i < 4; i++) {
			if (bit2Interruptor[i] == 0) {
				sendPair(0);
			} else {
				sendPair(1);
			}
		}

		// cut data latch
		digitalWrite(pin, 1);
		// wait 275μs
		delayMicroseconds(275);
		// 2 2675μs lock to signal the closure of the signal
		digitalWrite(pin, 0);
	}

	/**
	 * send bits as pair values
	 * @param b
	 */
	private void sendPair(int b) {
		if (b == 1) {
			sendBit(pin, 1);
			sendBit(pin, 0);
		} else {
			sendBit(pin, 0);
			sendBit(pin, 1);
		}
	}

	/**
	 * send raw bit
	 * @param b
	 */
	private static void sendBit(int pin, int value) {
		if (value == 1) {
			digitalWrite(pin, 1);
			delayMicroseconds(310);   //275 orinally, but tweaked.
			digitalWrite(pin, 0);
			delayMicroseconds(1340);  //1225 orinally, but tweaked.
		} else {
			digitalWrite(pin, 1);
			delayMicroseconds(310);   //275 orinally, but tweaked.
			digitalWrite(pin, 0);
			delayMicroseconds(310);   //275 orinally, but tweaked.
		}
	}

	/**
	 * write to pin
	 * @param b
	 */
	private static void digitalWrite(int pin, int value) {
		try {
			Gpio.digitalWrite(pin, value);
		} catch(Throwable e) {
			/**
			 * catch this exception in order to
			 * test this code on pc plateform, delayMicroseconds only existe
			 * in wiringPi library
			 */
		}
	}

	/**
	 * Calculating the number 2 ^ stated figure, used by itob function to convert decimal / binary
	 * @param power
	 * @return
	 */
	private static long power2(int power) {
		long integer = 1;
		for (int i = 0; i < power; i++) {
			integer *= 2;
		}
		return integer;
	}

	/**
	 * Converts a binary number, the number required,
	 * and the number of bits desired output (here 26)
	 * Store the result in the overall picture "bit2"
	 * 
	 * @param integer
	 * @param length
	 * @return 
	 */
	private static String itob(long integer, int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if ((integer / power2(arr.length - 1 - i)) == 1) {
				integer -= power2(arr.length - 1 - i);
				arr[i] = 1;
			} else
				arr[i] = 0;
		}
		StringBuilder sb = new StringBuilder();
		int sz = 0;
		for (int b : arr) {
			if((sz % 2)==0) {
				sb.append(" ");
			}
			sb.append(b);
			sz++;
		}
		return sb.toString();
	}
	
	/**
	 * set pin for this helper
	 * @param pin
	 * @return DioHelper
	 */
	public DioHelper pin(int pin) {
		setPin(pin);
		if(!simulation) {
			Gpio.pinMode(pin, 1);
		}
		return this;
	}

	/**
	 * set pin for this helper
	 * @param sender
	 * @return DioHelper
	 */
	public DioHelper sender(int sender) {
		/**
		 * no change
		 */
		if(this.sender == sender) return this;
		this.sender = sender;
		logger.info("[SENDER] {}", itob(sender, bit2sender));
		return this;
	}

	/**
	 * set pin for this helper
	 * @param interruptor
	 * @return DioHelper
	 */
	public DioHelper interruptor(int interruptor) {
		/**
		 * no change
		 */
		if(this.interruptor == interruptor) return this;
		this.interruptor = interruptor;
		logger.info("[INTRUP] {}", itob(interruptor, bit2Interruptor));
		return this;
	}


	/**
	 * static launcher
	 * @param argv
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public static void main(String[] argv) throws InterruptedException, IOException {
		logger.warn("args {}", (Object) argv);
		DioHelper dioHelper = new DioHelper();

		if(simulation) {
			/**
			 * fix pin
			 */
			dioHelper
				.pin(Integer.parseInt(argv[0]))
				.sender(Integer.parseInt(argv[2]))
				.interruptor(Integer.parseInt(argv[1]));
			
				if(argv[3].startsWith("on")) {
					dioHelper.switchOn();
				}
				if(argv[3].startsWith("off")) {
					dioHelper.switchOff();
				}
		} else {
			if(Gpio.piHiPri(99) != -1) {
				if(dioHelper.wiringPiSetup()) {
					/**
					 * fix pin
					 */
					dioHelper
						.pin(Integer.parseInt(argv[0]))
						.sender(Integer.parseInt(argv[2]))
						.interruptor(Integer.parseInt(argv[1]));
					
						if(argv[3].startsWith("on")) {
							dioHelper.switchOn();
						}
						if(argv[3].startsWith("off")) {
							dioHelper.switchOff();
						}
				} else {
					logger.warn("Unable to detect wiring PI");
				}
				Gpio.piHiPri(0);
			} else {
				throw new InterruptedException("Unable to set priority");
			}
			Gpio.piHiPri(0);
		}
	}
}
