package com.example.springsecuritywithcognito.service;

import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.exception.UserNotFoundException;
import com.example.springsecuritywithcognito.repository.UserRepository;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

	public AuthenticatedResponse changeTmpPassword(ChangePasswordReqeustDto request) {
		User user = userRepository.findByUsername(request.getUsername()).orElseThrow(UserNotFoundException::new);

		AdminRespondToAuthChallengeResult result =
				cognitoService.adminRespondToAuthChallenge(request.getUsername(), request.getPassword(), request.getSession()).get();
		user.setLastSignInAt(LocalDateTime.now());
		userRepository.save(user);
		return new AuthenticatedResponse(result.getAuthenticationResult().getAccessToken());
	}
}
