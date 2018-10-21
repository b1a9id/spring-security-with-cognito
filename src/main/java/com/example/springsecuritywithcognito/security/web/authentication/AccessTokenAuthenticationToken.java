package com.example.springsecuritywithcognito.security.web.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;

	private final Object credentials;

	public AccessTokenAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);

		if (credentials == null) {
			throw new IllegalArgumentException("Cannot credentials null to constructor");
		}
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
