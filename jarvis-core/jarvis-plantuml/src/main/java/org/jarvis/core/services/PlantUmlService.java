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

package org.jarvis.core.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;

import org.jarvis.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

/**
 * main daemon
 */
@Component
public class PlantUmlService {
	protected Logger logger = LoggerFactory.getLogger(PlantUmlService.class);

	@Autowired
	Environment env;
	
	/**
	 * init
	 */
	@PostConstruct
	public void init() {
		GraphvizUtils.setDotExecutable(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env.getProperty("jarvis.dot.executable")));
	}

	/**
	 * render as svg
	 * @param payload
	 * @return String
	 * @throws TechnicalException
	 */
	public String svg(String payload) throws TechnicalException {
		SourceStringReader reader = new SourceStringReader(payload);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		try {
			reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
		} catch (IOException e) {
			throw new TechnicalException(e);
		}

		// The XML is stored into svg
		final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
		return svg;
	}
}
