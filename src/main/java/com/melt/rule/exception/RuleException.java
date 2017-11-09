package com.melt.rule.exception;

public class RuleException extends Exception {

	private static final long serialVersionUID = 2679400870494156039L;

	public RuleException() {
		super();
	}

	public RuleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleException(String message) {
		super(message);
	}

	public RuleException(Throwable cause) {
		super(cause);
	}
	
	

}
