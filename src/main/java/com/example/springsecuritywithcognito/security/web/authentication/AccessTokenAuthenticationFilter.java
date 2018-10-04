package com.example.springsecuritywithcognito.security.web.authentication;

import com.example.springsecuritywithcognito.security.authentication.AccessTokenAuthenticationToken;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccessTokenAuthenticationFilter extends BasicAuthenticationFilter {

	public AccessTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (StringUtils.isEmpty(accessToken) || !accessToken.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		try {
			Authentication authentication = this.getAuthenticationManager()
					.authenticate(new AccessTokenAuthenticationToken(accessToken.split(" ")[1], null));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (AuthenticationException e) {
			logger.warn("authentication failed.", e);
			SecurityContextHolder.clearContext();
		}

		chain.doFilter(request, response);
	}
}
