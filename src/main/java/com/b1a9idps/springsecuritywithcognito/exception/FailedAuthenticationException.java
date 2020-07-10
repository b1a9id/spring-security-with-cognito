package com.b1a9idps.springsecuritywithcognito.exception;

@SuppressWarnings("serial")
public class FailedAuthenticationException extends ServiceException {

	public FailedAuthenticationException() {
		super();
	}

	public FailedAuthenticationException(String message) {
		super(message);
	}

	public FailedAuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
