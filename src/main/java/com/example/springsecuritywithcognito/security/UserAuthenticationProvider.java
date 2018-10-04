package com.example.springsecuritywithcognito.security;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.exception.FailedAuthenticationException;
import com.example.springsecuritywithcognito.exception.FirstTimeLoginException;
import com.example.springsecuritywithcognito.repository.UserRepository;
import com.example.springsecuritywithcognito.security.core.userdetails.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.service.CognitoService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

	private final UserRepository userRepository;

	private final CognitoService cognitoService;

	public UserAuthenticationProvider(UserRepository userRepository, CognitoService cognitoService) {
		this.userRepository = userRepository;
		this.cognitoService = cognitoService;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);

		String username = String.valueOf(authentication.getPrincipal());
		String password = String.valueOf(authentication.getCredentials());

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("username '" + username + "' not found"));

		AdminInitiateAuthResult result;
		try {
			result = cognitoService.adminInitiateAuth(username, password).get();
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException("username '" + username + "' not found", e);
		} catch (FailedAuthenticationException e) {
			throw new BadCredentialsException("authentication failed", e);
		}

		// 初回ログイン時は、パスワードの変更が必要
		if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
			throw new FirstTimeLoginException(result.getSession());
		}

		UserDetails userDetails = new AuthenticatedUserDetails(user, result.getAuthenticationResult().getAccessToken());
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
