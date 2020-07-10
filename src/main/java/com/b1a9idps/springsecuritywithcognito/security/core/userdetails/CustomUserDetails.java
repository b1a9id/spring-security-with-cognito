package com.b1a9idps.springsecuritywithcognito.security.core.userdetails;

import com.b1a9idps.springsecuritywithcognito.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

	private final User user;

	private final String accessToken;

	public CustomUserDetails(User user, String accessToken) {
		this.user = user;
		this.accessToken = accessToken;
	}

	public User getUser() {
		return user;
	}

	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList("ROLE_" + this.user.getRole().name());
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
