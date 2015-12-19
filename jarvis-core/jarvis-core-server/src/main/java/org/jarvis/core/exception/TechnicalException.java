package org.jarvis.core.exception;

public class TechnicalException extends RuntimeException {

	public TechnicalException(InstantiationException e) {
		super(e);
	}

	public TechnicalException(IllegalAccessException e) {
		super(e);
	}

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 8605302097434223980L;

}
