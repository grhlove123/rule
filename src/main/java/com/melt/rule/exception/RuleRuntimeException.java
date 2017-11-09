package com.melt.rule.exception;

public class RuleRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -2213817021227794584L;

	public RuleRuntimeException() {
		super();
	}

	public RuleRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RuleRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleRuntimeException(String message) {
		super(message);
	}

	public RuleRuntimeException(Throwable cause) {
		super(cause);
	}
	
	

}
