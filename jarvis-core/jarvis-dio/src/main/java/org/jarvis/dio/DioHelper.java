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

import java.util.EnumSet;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
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
	private GpioPinDigitalOutput pinGpio;
	
	/**
	 * setter
	 * @param pin
	 */
	public void setPin(int pin) {
		if(this.pin == pin) return;
		this.pin = pin;
		switch(pin) {
			case 0:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00);
				break;
			case 1:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01);
				break;
			case 2:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02);
				break;
			case 3:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03);
				break;
			case 4:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
				break;
			case 5:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
				break;
			case 6:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06);
				break;
			case 7:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07);
				break;
			case 8:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08);
				break;
			case 9:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_19);
				break;
			case 10:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10);
				break;
			case 11:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11);
				break;
			case 12:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12);
				break;
			case 13:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13);
				break;
			case 14:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14);
				break;
			case 15:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15);
				break;
			case 16:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16);
				break;
			case 17:
				pinGpio = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_17);
				break;
			default:
				break;
		}
		
		pinGpio.setShutdownOptions(true, PinState.LOW);

		logger.info("Select pin {} {}", pin, pinGpio);
	}


	private int sender;
	private int interruptor;
	private int bit2sender[] = new int[26];     // 26 bit sender identifier
	private int bit2Interruptor[] = new int[4]; // 4 bit interuptor

	protected static GpioController gpio;
	
	/**
	 * default constructor
	 */
	public DioHelper() {
		init();
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
	
	GpioPinDigitalOutput mockedPin;
	
	boolean init() throws RuntimeException {
		try {
			try {
				// create gpio controller
		        gpio = GpioFactory.getInstance();
			} catch(Throwable e) {
				throw new Exception(e);
			}
		} catch(Exception e) {
			gpio = Mockito.mock(GpioController.class);
			mockedPin = Mockito.mock(GpioPinDigitalOutput.class);
			Mockito.when(gpio.provisionDigitalOutputPin((Pin) Matchers.any(Pin.class))).thenReturn(mockedPin);
			return true;
		}
		return true;
	}

	/**
	 * activate switch
	 * @throws InterruptedException
	 */
	public void switchOn() throws InterruptedException {
		for(int i=0;i<5;i++) {
			transmit(1);
			Thread.sleep(10);
		}
	}

	/**
	 * un-activate switch
	 * @throws InterruptedException
	 */
	public void switchOff() throws InterruptedException {
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
		try {
			Gpio.delayMicroseconds(nanos);
		} catch(Throwable e) {
			
		}
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
		if(b == 1) {
			pinGpio.high();
		} else {
			pinGpio.low();
		}
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
	 * set pin for this helper
	 * @param pin
	 * @return DioHelper
	 */
	public DioHelper pin(int pin) {
		setPin(pin);
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
	 */
	public static void main(String[] argv) throws InterruptedException {
		DioHelper dioHelper = new DioHelper()
				.pin(Integer.parseInt(argv[0]))
				.sender(Integer.parseInt(argv[1]))
				.interruptor(Integer.parseInt(argv[2]));
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
