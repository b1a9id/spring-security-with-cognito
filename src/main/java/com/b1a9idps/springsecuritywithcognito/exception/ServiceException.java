package com.b1a9idps.springsecuritywithcognito.exception;

@SuppressWarnings("serial")
public class ServiceException extends RuntimeException {
    
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
    }

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}