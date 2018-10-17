package com.example.springsecuritywithcognito.security.core.userdetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomAuthenticationUserDetailsService implements AuthenticationUserDetailsService {

	private final CustomUserDetailsService userDetailsService;

	public CustomAuthenticationUserDetailsService(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {
		String username = token.getPrincipal().toString();
		String accessToken = token.getCredentials().toString();

		return Optional.ofNullable(userDetailsService.loadUserByUsername(username))
				.map(userDetails -> new CustomUserDetails(((CustomUserDetails) userDetails).getUser(), accessToken))
				.orElseThrow(() -> new UsernameNotFoundException("username '" + username + "' not found"));
	}
}
