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

package org.jarvis.runtime;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple capture stream
 */
public class CaptureStream extends Thread implements Runnable {

	protected Logger logger = LoggerFactory.getLogger(CaptureStream.class);
	private StringBuilder streamData = new StringBuilder();
	private InputStream stream;

	/**
	 * contructor
	 * 
	 * @param stream
	 */
	public CaptureStream(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		try {
			byte[] buffer = new byte[1];
			int chunk = stream.read(buffer);
			if (chunk >= 0) {
				do {
					streamData.append((char) buffer[0]);
					chunk = stream.read(buffer);
				} while (chunk >= 0);
			}
		} catch (IOException e) {
			logger.error("Error {}", e);
		}

	}

	/**
	 * raw getter
	 * 
	 * @return StringBuilder
	 */
	public StringBuilder getStreamData() {
		return streamData;
	}

	/**
	 * parse output as lines
	 * 
	 * @return String[]
	 */
	public String[] getValues() {
		return streamData.toString().split(System.lineSeparator());
	}

	@Override
	public String toString() {
		return "CaptureStream [streamData=" + streamData.length() + ", stream="
				+ stream + "]";
	}

}
