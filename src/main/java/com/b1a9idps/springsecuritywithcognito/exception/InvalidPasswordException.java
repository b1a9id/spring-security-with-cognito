package com.b1a9idps.springsecuritywithcognito.exception;

@SuppressWarnings("serial")
public class InvalidPasswordException extends ServiceException {

	public InvalidPasswordException() {}

	public InvalidPasswordException(String message) {
		super(message);
	}

	public InvalidPasswordException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
