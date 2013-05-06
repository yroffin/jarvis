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

package org.jarvis.main.parser;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jarvis.main.antlr4.aimlLexer;
import org.jarvis.main.exception.AimlParsingError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AimlLexerImpl extends aimlLexer {
	Logger logger = LoggerFactory.getLogger(AimlLexerImpl.class);

	public AimlLexerImpl(CharStream input) {
		super(input);
	}

	protected static CommonTokenStream getTokens(String filename)
			throws AimlParsingError {
		AimlLexerImpl lexer;
		try {
			lexer = new AimlLexerImpl(new ANTLRFileStream(filename, "UTF8"));
		} catch (IOException e) {
			throw new AimlParsingError(e);
		}
		return new CommonTokenStream(lexer);
	}

	protected static CommonTokenStream getTokensFromString(String data)
			throws AimlParsingError {
		AimlLexerImpl lexer;
		try {
			lexer = new AimlLexerImpl(new ANTLRFileStream(data));
		} catch (IOException e) {
			e.printStackTrace();
			throw new AimlParsingError(e);
		}
		return new CommonTokenStream(lexer);
	}

}
