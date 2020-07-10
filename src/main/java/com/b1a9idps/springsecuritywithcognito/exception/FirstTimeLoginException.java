package com.b1a9idps.springsecuritywithcognito.exception;

@SuppressWarnings("serial")
public class FirstTimeLoginException extends ServiceException {

    public FirstTimeLoginException() {
        super();
    }

    public FirstTimeLoginException(String message) {
        super(message);
    }

    public FirstTimeLoginException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
