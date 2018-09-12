package com.example.springsecuritywithcognito.security.handler;

import com.amazonaws.services.cognitoidp.model.PasswordResetRequiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticateFailuerHandler extends ExceptionMappingAuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		Map<String, String> exceptionMappings = new HashMap<>();
		exceptionMappings.put(PasswordResetRequiredException.class.getName(), "/users/change-password");
		setExceptionMappings(exceptionMappings);
		super.onAuthenticationFailure(request, response, exception);
	}
}
