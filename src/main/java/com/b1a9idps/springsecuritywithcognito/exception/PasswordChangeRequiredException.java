package com.b1a9idps.springsecuritywithcognito.exception;

import org.springframework.security.core.AuthenticationException;

@SuppressWarnings("serial")
public class PasswordChangeRequiredException extends AuthenticationException {

    public PasswordChangeRequiredException(String message) {
        super(message);
    }

    public PasswordChangeRequiredException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
