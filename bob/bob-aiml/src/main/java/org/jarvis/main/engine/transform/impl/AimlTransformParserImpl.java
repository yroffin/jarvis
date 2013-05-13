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
import java.util.ArrayList;
import java.util.List;

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
	Logger								logger				= LoggerFactory
																	.getLogger(AimlTransformParserImpl.class);

	private static final String			CONST_UNDERSCORE	= "_";
	private static final String			CONST_STAR			= "*";
	protected AimlTransformLexerImpl	lexer				= null;

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

	private static boolean	parsingDebug	= false;

	@Override
	public void onNewSentence() {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewSentence");
		}
		last = new TransformedItemImpl();
		tx.add(last);
	}

	@Override
	public void onNewStar(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewStar [" + value + "]");
		}
		last.add(CONST_STAR);
	}

	@Override
	public void onNewUnderscore(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewUnderscore [" + value + "]");
		}
		last.add(CONST_UNDERSCORE);
	}

	@Override
	public void onNewWord(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewWord [" + value + "]");
		}
		last.add(value.toUpperCase());
	}

	@Override
	public void onNewFilename(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewFilename [" + value + "]");
		}
		last.add(value.toUpperCase().replace(".", " DOT "));
	}

	@Override
	public void onNewUrl(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewUrl [" + value + "]");
		}
		last.add(value.toUpperCase().replace("://", " ").replace(".", " DOT "));
	}

	@Override
	public void onNewAbrev(String value) {
		if (logger.isDebugEnabled() && parsingDebug) {
			logger.debug("onNewAbrev [" + value + "]");
		}
		last.add(value.toUpperCase().replace("THAT'S", "THAT IS")
				.replace("DON'T", "DO NOT"));
	}

	private ITransformedItem		last;
	private List<ITransformedItem>	tx	= null;

	@Override
	public List<ITransformedItem> parse() throws AimlParsingError {
		try {
			tx = new ArrayList<ITransformedItem>();
			if (_input.getText().length() > 0) {
				document();
			} else {
				/**
				 * empty document
				 */
				onNewSentence();
			}
			return tx;
		} catch (RecognitionException e) {
			throw new AimlParsingError(e);
		}
	}
}
