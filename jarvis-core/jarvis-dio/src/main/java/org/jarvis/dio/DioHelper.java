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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * DioHelper
 * Cf. http://pi4j.com/example/control.html
 */
public class DioHelper {

	/**
	 * logger
	 */
	protected static Logger logger = LoggerFactory.getLogger(DioHelper.class);
	
	private int pin;
	private int sender;
	private int interruptor;
	private int bit2sender[] = new int[26];     // 26 bit sender identifier
	private int bit2Interruptor[] = new int[4]; // 4 bit interuptor

	private static GpioController gpio;
	
	/**
	 * constructor
	 * @param pin
	 * @param sender
	 * @param interruptor
	 */
	DioHelper(int pin, int sender, int interruptor) {
		this.pin = pin;
		this.sender = sender;
		this.interruptor = interruptor;
	}
	
	boolean init() {
		try {
			// create gpio controller
	        gpio = GpioFactory.getInstance();
		} catch(UnsatisfiedLinkError e) {
			return false;
		}

		logger.info("Pin {} Sender {} Interuptor {}", this.pin, this.sender, this.interruptor);
		logger.info("Fix pin {} to output mode", pin);
		/**
		 * TODO
		 * fix pin pin in OUTPUT mode
		 */
		
		/**
		 * converting the code of the transmitter in binary code
		 */
		logger.info("[SENDER] {}", itob(sender, bit2sender));
		logger.info("[INTRUP] {}", itob(interruptor, bit2Interruptor));
		return true;
	}

	/**
	 * activate switch
	 * @throws InterruptedException
	 */
	void switchOn() throws InterruptedException {
		for(int i=0;i<5;i++) {
			transmit(1);
			Thread.sleep(10);
		}
	}

	/**
	 * un-activate switch
	 * @throws InterruptedException
	 */
	void switchOff() throws InterruptedException {
		for(int i=0;i<5;i++) {
			transmit(0);
			Thread.sleep(10);
		}
	}

	/**
	 * wait on micro second
	 * @param nanos
	 */
	private static void delayMicroseconds(long nanos) {
	  long elapsed;
	  final long startTime = System.nanoTime();
	  do {
	    elapsed = System.nanoTime() - startTime;
	  } while (elapsed < nanos * 1000);
	}
	
	/**
	 * transmit value
	 * @param value
	 */
	private void transmit(int value) {
		int i;

		// Lock Sequence announcing the starting signal to the receiver
		logger.info("Lock Sequence announcing the starting signal to the receiver on pin {}", pin);
		write(1);
		delayMicroseconds(275);  // a noise bit before starting to put the delays of the receiver 0
		write(0);
		delayMicroseconds(9900); // 9900μs first lock
		write(1);    // high again
		delayMicroseconds(275);  // pending 275μs between two locks
		write(0);    // 2675μs second lock
		delayMicroseconds(2675);
		write(1);    // Returning to high to cut the locks of well data

		// Transmitter sends the code (272946 = 1000010101000110010 binary)
		logger.info("Transmitter sends the code {}", bit2sender);
		for (i = 0; i < 26; i++) {
			sendPair(bit2sender[i]);
		}

		// Sends the bit defining whether it is a control group or not (26th bit)
		logger.info("Sends the bit defining whether it is a control group or not (26th bit) {}", 0);
		sendPair(0);

		// Sends the bit defining whether it is on or extinguished 27th bit)
		logger.info("Sends the bit defining whether it is on or extinguished 27th bit) {}", value);
		sendPair(value);

		// Sends the last 4 bits, representing the switch code here 0 (4 encodes bit so 0000)
		// note: on official chacon remote controls, switches are logically named from 0 to x
		// switch 1 = 0 (ie 0000), switch 2 = 1 (1000), switch 3 = 2 (0100), etc ...
		logger.info("Sends the last 4 bits {}", bit2Interruptor);
		for (i = 0; i < 4; i++) {
			if (bit2Interruptor[i] == 0) {
				sendPair(0);
			} else {
				sendPair(1);
			}
		}

		logger.info("End");
		write(1);   // cut data latch
		delayMicroseconds(275); // wait 275μs
		write(0);   // 2 2675μs lock to signal the closure of the signal
	}

	/**
	 * send bits as pair values
	 * @param b
	 */
	private void sendPair(int b) {
		logger.info("[PAIR] {}", b);
		if (b == 1) {
			sendBit(1);
			sendBit(0);
		} else {
			sendBit(0);
			sendBit(1);
		}
	}

	/**
	 * send raw bit
	 * @param b
	 */
	private void sendBit(int b) {
		logger.info("[BITS] {}", b);
		if (b == 1) {
			write(1);
			delayMicroseconds(310);   //275 orinally, but tweaked.
			write(0);
			delayMicroseconds(1340);  //1225 orinally, but tweaked.
		} else {
			write(1);
			delayMicroseconds(310);   //275 orinally, but tweaked.
			write(0);
			delayMicroseconds(310);   //275 orinally, but tweaked.
		}
	}

	/**
	 * write to pin
	 * @param b
	 */
	private void write(int b) {
		logger.debug("[RASPBERRY] pin {} set to {}", pin, b);
	}

	/**
	 * Calculating the number 2 ^ stated figure, used by itob function to convert decimal / binary
	 * @param power
	 * @return
	 */
	private long power2(int power) {
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
	private String itob(long integer, int[] arr) {
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
	 * static launcher
	 * @param argv
	 * @throws InterruptedException
	 */
	public static void main(String[] argv) throws InterruptedException {
		DioHelper dioHelper = new DioHelper(Integer.parseInt(argv[0]),Integer.parseInt(argv[1]),Integer.parseInt(argv[2]));
		if(dioHelper.init()) {
			if(argv[3].startsWith("on")) {
				dioHelper.switchOn();
			}
			if(argv[3].startsWith("off")) {
				dioHelper.switchOff();
			}
		} else {
			logger.warn("Unable to detect wiring PI");
		}
	}
}
