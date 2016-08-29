package com.renren.wan.monitor;

public class CheckedException extends RuntimeException {

	private static final long serialVersionUID = 8806681950576562633L;

	public CheckedException() {
	}

	public CheckedException(String message) {
		super(message);
	}

	public CheckedException(Throwable cause) {
		super(cause);
	}

	public CheckedException(String message, Throwable cause) {
		super(message, cause);
	}

}
