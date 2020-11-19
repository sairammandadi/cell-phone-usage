package com.company.cellphone.exception;

public class WriteFailureException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WriteFailureException() {
		super();
	}

	public WriteFailureException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WriteFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public WriteFailureException(String message) {
		super(message);
	}

	public WriteFailureException(Throwable cause) {
		super(cause);
	}
}
