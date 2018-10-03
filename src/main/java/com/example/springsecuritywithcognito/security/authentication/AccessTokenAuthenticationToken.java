package com.example.springsecuritywithcognito.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {

	public final Object principal;

	public AccessTokenAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);

		if (principal == null) {
			throw new IllegalArgumentException("Cannot principal null to constructor");
		}

		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
