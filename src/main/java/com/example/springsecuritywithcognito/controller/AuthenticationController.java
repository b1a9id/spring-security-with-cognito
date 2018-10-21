package com.example.springsecuritywithcognito.controller;

import com.example.springsecuritywithcognito.controller.dto.request.ChangePasswordRequest;
import com.example.springsecuritywithcognito.controller.dto.request.LoginRequest;
import com.example.springsecuritywithcognito.exception.FirstTimeLoginException;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.request.LoginRequestDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.FirstLoginResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

	private final UserService userService;

	public AuthenticationController(UserService userService) {
		this.userService = userService;
	}

	@PatchMapping("change-password")
	public AuthenticatedResponse changePassword(@RequestBody @Validated ChangePasswordRequest request) {
		ChangePasswordReqeustDto dto = new ChangePasswordReqeustDto();
		BeanUtils.copyProperties(request, dto, null, "confirmationPassword");
		return userService.changeTmpPassword(dto);
	}

	@PostMapping("login")
	public AuthenticatedResponse login(@RequestBody @Validated LoginRequest loginRequest) {
		LoginRequestDto dto = new LoginRequestDto();
		BeanUtils.copyProperties(loginRequest, dto);
		return userService.authentication(dto);
	}

	@ExceptionHandler(FirstTimeLoginException.class)
	public ResponseEntity<FirstLoginResponse> firstTimeLoginExceptionHandler(FirstTimeLoginException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new FirstLoginResponse(e.getMessage()));
	}
}
