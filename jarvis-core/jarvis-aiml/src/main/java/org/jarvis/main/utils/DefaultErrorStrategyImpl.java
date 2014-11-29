/**
 *  Copyright 2012 Yannick Roffin
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

package org.jarvis.main.utils;

import java.io.File;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.jarvis.main.engine.impl.transform.AimlTransformParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultErrorStrategyImpl extends DefaultErrorStrategy {
	protected Logger logger = LoggerFactory
			.getLogger(DefaultErrorStrategyImpl.class);

	private final File filename;
	private final IAimlParser parser;

	private String text;

	public DefaultErrorStrategyImpl(IAimlParser parser, File filename) {
		this.filename = filename;
		this.parser = parser;
	}

	public DefaultErrorStrategyImpl(AimlTransformParserImpl parser, String text) {
		this.filename = null;
		this.text = text;
		this.parser = parser;
	}

	@Override
	public Token recoverInline(Parser recognizer) throws RecognitionException {
		if (filename != null) {
			logger.error(filename.getAbsolutePath());
		} else {
			logger.error(text);
		}
		logger.error(parser.getStatistics());
		logger.error(recognizer.getContext().toInfoString(recognizer));
		return super.recoverInline(recognizer);
	}

	@Override
	public void reportError(Parser recognizer, RecognitionException e)
			throws RecognitionException {
		super.reportError(recognizer, e);
		logger.error(e.getMessage());
		parser.addParserError(1);
	}

}
