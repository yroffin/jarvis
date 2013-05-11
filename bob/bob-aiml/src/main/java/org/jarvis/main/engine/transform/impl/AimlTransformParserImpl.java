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

package org.jarvis.main.engine.transform.impl;

import java.io.File;

import org.antlr.v4.runtime.RecognitionException;
import org.jarvis.main.antlr4.normalizerParser;
import org.jarvis.main.engine.transform.IAimlTransformParser;
import org.jarvis.main.exception.AimlParsingError;
import org.jarvis.main.model.transform.ITransformedItem;
import org.jarvis.main.model.transform.impl.TransformedItemImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlTransformParserImpl extends normalizerParser implements
		IAimlTransformParser {
	Logger								logger	= LoggerFactory
														.getLogger(AimlTransformParserImpl.class);

	protected AimlTransformLexerImpl	lexer	= null;

	/**
	 * AIML model
	 */

	/**
	 * default construtor
	 * 
	 * @param filename
	 * @param encoding
	 * @throws AimlParsingError
	 */
	public AimlTransformParserImpl(File filename, String encoding)
			throws AimlParsingError {
		super(AimlTransformLexerImpl.getTokens(filename.getAbsolutePath(),
				encoding));
		lexer = (AimlTransformLexerImpl) _input.getTokenSource();
	}

	/**
	 * another default constructor
	 * 
	 * @param local
	 * @throws AimlParsingError
	 */
	public AimlTransformParserImpl(String data) throws AimlParsingError {
		super(AimlTransformLexerImpl.getTokensFromString(data));
		lexer = (AimlTransformLexerImpl) _input.getTokenSource();
	}

	private static boolean	parsingDebug	= true;

	@Override
	public void onNewWord(String value) {
		super.onNewWord(value);
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewWord [" + value + "]");
		}
		tx.add(value.toUpperCase());
	}

	@Override
	public void onNewSentence() {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewSentence");
		}
		tx.add();
	}

	@Override
	public void onNewFilename(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewFilename [" + value + "]");
		}
		tx.add(value.toUpperCase().replace(".", " DOT "));
	}

	@Override
	public void onNewUrl(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewUrl [" + value + "]");
		}
		tx.add(value.toUpperCase().replace("://", " ").replace(".", " DOT "));
	}

	@Override
	public void onNewAbrev(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewAbrev [" + value + "]");
		}
		tx.add(value.toUpperCase().replace("THAT'S", "THAT IS")
				.replace("DON'T", "DO NOT"));
	}

	private ITransformedItem	tx	= null;

	@Override
	public ITransformedItem parse() throws AimlParsingError {
		try {
			tx = new TransformedItemImpl();
			document();
			return tx;
		} catch (RecognitionException e) {
			throw new AimlParsingError(e);
		}
	}
}
