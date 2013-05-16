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

package org.jarvis.main.engine.impl.transform;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jarvis.main.antlr4.normalizerLexer;
import org.jarvis.main.exception.AimlParsingError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlTransformLexerImpl extends normalizerLexer {
	Logger	logger	= LoggerFactory.getLogger(AimlTransformLexerImpl.class);

	/**
	 * default constructor
	 * 
	 * @param input
	 */
	public AimlTransformLexerImpl(CharStream input) {
		super(input);
	}

	/**
	 * build from file
	 * 
	 * @param filename
	 * @param encoding
	 * @return
	 * @throws AimlParsingError
	 */
	protected static CommonTokenStream getTokens(String filename,
			String encoding) throws AimlParsingError {
		AimlTransformLexerImpl lexer;
		try {
			lexer = new AimlTransformLexerImpl(new ANTLRFileStream(filename,
					encoding));
		} catch (IOException e) {
			throw new AimlParsingError(e);
		}
		return new CommonTokenStream(lexer);
	}

	/**
	 * build from string
	 * 
	 * @param data
	 * @return
	 * @throws AimlParsingError
	 */
	protected static CommonTokenStream getTokensFromString(String data)
			throws AimlParsingError {
		AimlTransformLexerImpl lexer;
		lexer = new AimlTransformLexerImpl(new ANTLRInputStream(data));
		return new CommonTokenStream(lexer);
	}

}
