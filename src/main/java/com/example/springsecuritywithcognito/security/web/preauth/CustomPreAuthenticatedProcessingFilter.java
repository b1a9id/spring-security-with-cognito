package com.example.springsecuritywithcognito.security.web.preauth;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class CustomPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		return "";
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (StringUtils.isEmpty(accessToken) || !accessToken.startsWith("Bearer ")) {
			return "";
		}
		return accessToken.split(" ")[1];
	}
}
