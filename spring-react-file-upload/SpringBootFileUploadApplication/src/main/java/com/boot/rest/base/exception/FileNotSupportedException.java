package com.boot.rest.base.exception;

public class FileNotSupportedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileNotSupportedException(String message) {
		super(message);
	}
}
