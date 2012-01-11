package com.alexalecu.imageCrop.exception;

public class InvalidOperationException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidOperationException() {
		super();
	}

	public InvalidOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidOperationException(String message) {
		super(message);
	}

	public InvalidOperationException(Throwable cause) {
		super(cause);
	}

}
