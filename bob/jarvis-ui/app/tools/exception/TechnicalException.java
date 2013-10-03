package tools.exception;

import org.jarvis.main.exception.AimlParsingError;

@SuppressWarnings("serial")
public class TechnicalException extends Exception {

	public TechnicalException(AimlParsingError e) {
		super(e);
	}

}
