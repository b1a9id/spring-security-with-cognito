package com.example.springsecuritywithcognito.service;

import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.exception.FailedAuthenticationException;
import com.example.springsecuritywithcognito.exception.FirstTimeLoginException;
import com.example.springsecuritywithcognito.exception.UserNotFoundException;
import com.example.springsecuritywithcognito.repository.UserRepository;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.request.LoginRequestDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.UserResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final CognitoService cognitoService;

	public UserService(UserRepository userRepository, CognitoService cognitoService) {
		this.userRepository = userRepository;
		this.cognitoService = cognitoService;
	}

	public UserResponse get(Integer id) {
		User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
		cognitoService.adminGetUser(user.getUsername()).orElseThrow(UserNotFoundException::new);
		return UserResponse.of(user);
	}

	public void updateLastSignInAt(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("username " + username + "not found"));
		user.setLastSignInAt(LocalDateTime.now());
		userRepository.save(user);
}

	@PreAuthorize("isAnonymous()")
	public AuthenticatedResponse changeTmpPassword(ChangePasswordReqeustDto request) {
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);

		AdminRespondToAuthChallengeResult result =
				cognitoService.adminRespondToAuthChallenge(request.getUsername(), request.getPassword(), request.getSession()).get();
		user.setLastSignInAt(LocalDateTime.now());
		userRepository.save(user);
		return new AuthenticatedResponse(request.getUsername(), result.getAuthenticationResult().getAccessToken());
	}

	public AuthenticatedResponse authentication(LoginRequestDto request) {
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);

		AdminInitiateAuthResult result;
		try {
			result = cognitoService.adminInitiateAuth(request.getUsername(), request.getPassword()).get();
		} catch (UserNotFoundException e) {
			throw new UsernameNotFoundException("username '" + request.getUsername() + "' not found", e);
		} catch (FailedAuthenticationException e) {
			throw new BadCredentialsException("authentication failed", e);
		}

		// 初回ログイン時は、パスワードの変更が必要
		if (ObjectUtils.nullSafeEquals(NEW_PASSWORD_REQUIRED.name(), result.getChallengeName())) {
			throw new FirstTimeLoginException(result.getSession());
		}

		user.setLastSignInAt(LocalDateTime.now());
		userRepository.save(user);
		return new AuthenticatedResponse(request.getUsername(), result.getAuthenticationResult().getAccessToken());
	}
}
