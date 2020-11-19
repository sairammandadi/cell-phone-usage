package com.company.cellphone.exception;

public class CSVReadFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CSVReadFailureException() {
		super();
	}

	public CSVReadFailureException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CSVReadFailureException(String message, Throwable cause) {
		super(message, cause);
	}

	public CSVReadFailureException(String message) {
		super(message);
	}

	public CSVReadFailureException(Throwable cause) {
		super(cause);
	}
}
