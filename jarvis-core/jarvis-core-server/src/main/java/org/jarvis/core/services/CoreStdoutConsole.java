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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.jarvis.core.JarvisCoreServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * main daemon
 */
@Component
public class CoreStdoutConsole extends OutputStream {
	protected static Logger logger = LoggerFactory.getLogger(JarvisCoreServer.class);
	private StringBuilder stringBuilder = new StringBuilder();

	private Level level = Level.WARN;

	@Autowired
	CoreMoquette coreMoquette;

	/**
	 * constructor
	 */
	public CoreStdoutConsole() {
		System.setOut(new PrintStream(this));
	}

	@Override
	public void write(int i) throws IOException {
		char c = (char) i;
		if (c == '\r' || c == '\n') {
			if (stringBuilder.length() > 0) {
				if (level .compareTo(Level.ERROR) == 0) {
					logger.error(stringBuilder.toString());
				}
				if (level.compareTo(Level.WARN) == 0) {
					logger.warn(stringBuilder.toString());
				}
				if (level.compareTo(Level.INFO) == 0) {
					logger.info(stringBuilder.toString());
				}
				if (level.compareTo(Level.DEBUG) == 0) {
					logger.debug(stringBuilder.toString());
				}
				if (level.compareTo(Level.TRACE) == 0) {
					logger.trace(stringBuilder.toString());
				}
				coreMoquette.publishMostOne("/system/console/stdout", stringBuilder.toString());
				stringBuilder = new StringBuilder();
			}
		} else
			stringBuilder.append(c);
	}
}
