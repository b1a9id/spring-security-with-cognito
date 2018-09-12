package com.example.springsecuritywithcognito.service;

import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.repository.UserRepository;
import com.example.springsecuritywithcognito.security.dto.AuthenticatedUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {
	private UserRepository userRepository;

	public AuthenticatedUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("username " + username + " not found"));
		return new AuthenticatedUserDetails(user, null);
	}
}
