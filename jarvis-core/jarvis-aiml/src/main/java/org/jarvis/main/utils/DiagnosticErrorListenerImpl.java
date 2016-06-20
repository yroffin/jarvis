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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.BitSet;

import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * listener
 */
public class DiagnosticErrorListenerImpl extends DiagnosticErrorListener {
	protected Logger logger = LoggerFactory
			.getLogger(DiagnosticErrorListener.class);

	private final File document;

	private final IAimlParser parser;

	private String text;

	/**
	 * @param parser
	 * @param document
	 */
	public DiagnosticErrorListenerImpl(IAimlParser parser, File document) {
		this.document = document;
		this.parser = parser;
	}

	/**
	 * @param parser
	 * @param text
	 */
	public DiagnosticErrorListenerImpl(IAimlParser parser, String text) {
		this.document = null;
		this.text = text;
		this.parser = parser;
	}

	/**
	 * read file
	 * @param line
	 * @return
	 */
	private String readFile(int line) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(document));
			String data;

			int i = 0;
			for (;(data = reader.readLine()) != null;) {
				if ((i + 1) == line) {
					reader.close();
					return data;
				}
				i++;
			}
			reader.close();
		} catch (IOException e) {
			logger.error("Error {}", e);
		}
		return "N/A";
	}

	@Override
	public void reportAmbiguity(@NotNull Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, @NotNull BitSet ambigAlts,
			@NotNull ATNConfigSet configs) {
		super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex,
				ambigAlts, configs);
	}

	@Override
	public void reportAttemptingFullContext(@NotNull Parser recognizer,
			@NotNull DFA dfa, int startIndex, int stopIndex,
			@NotNull ATNConfigSet configs) {
		if (parser.reportAttemptingFullContext()) {
			super.reportAttemptingFullContext(recognizer, dfa, startIndex,
					stopIndex, configs);
		}
	}

	@Override
	public void reportContextSensitivity(@NotNull Parser recognizer,
			@NotNull DFA dfa, int startIndex, int stopIndex,
			@NotNull ATNConfigSet configs) {
		super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex,
				configs);
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		super.syntaxError(recognizer, offendingSymbol, line,
				charPositionInLine, msg, e);
		if (document != null) {
			logger.error("Filename : " + document.getAbsolutePath());
			logger.error("line " + line + " char position "
					+ charPositionInLine + " : " + msg + " : " + readFile(line));
		} else {
			logger.error("line " + line + " char position "
					+ charPositionInLine + " : " + msg + " : " + text);
		}
		parser.addLexerError(1);
	}

}
