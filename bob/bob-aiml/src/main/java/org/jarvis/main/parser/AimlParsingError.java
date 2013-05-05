package org.jarvis.main.parser;

import java.io.IOException;

import org.antlr.v4.runtime.RecognitionException;

public class AimlParsingError extends Exception {

	/**
	 * default serial id
	 */
	private static final long serialVersionUID = 6352462504214710201L;

	public AimlParsingError(IOException e) {
		super(e);
	}

	public AimlParsingError(RecognitionException e) {
		super(e);
	}

}
