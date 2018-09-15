package com.example.springsecuritywithcognito.security.filter;

import com.example.springsecuritywithcognito.controller.dto.request.LoginRequest;
import com.example.springsecuritywithcognito.exception.FirstTimeLoginException;
import com.example.springsecuritywithcognito.exception.PasswordChangeRequiredException;
import com.example.springsecuritywithcognito.exception.UserNotFoundException;
import com.example.springsecuritywithcognito.utils.CookieUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		LoginRequest loginRequest;
		try {
			loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
		} catch (IOException e) {
			throw new RuntimeException();
		}

		String username = loginRequest.getUsername();
		if (StringUtils.isEmpty(username)) {
			throw new UserNotFoundException("username empty");
		}
		String password = loginRequest.getPassword();
		if (StringUtils.isEmpty(password)) {
			throw new BadCredentialsException("password empty");
		}

		try {
			return this.getAuthenticationManager()
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (FirstTimeLoginException e) {
			CookieUtils.addCookie(request, response, "session", e.getMessage());
			throw new PasswordChangeRequiredException("password change required", e);
		}
	}
}
