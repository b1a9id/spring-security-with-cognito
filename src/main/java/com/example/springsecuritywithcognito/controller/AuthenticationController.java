package com.example.springsecuritywithcognito.controller;

import com.example.springsecuritywithcognito.controller.dto.request.ChangePasswordRequest;
import com.example.springsecuritywithcognito.exception.FailedAuthenticationException;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomUserDetails;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.FirstLoginResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class AuthenticationController {

	private final UserService userService;

	public AuthenticationController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("first-login")
	public FirstLoginResponse firstLogin(HttpServletRequest request) {
		String session = WebUtils.getCookie(request, "session").getValue();
		return new FirstLoginResponse(session);
	}

	@PatchMapping("change-password")
	public AuthenticatedResponse changePassword(@RequestBody @Validated ChangePasswordRequest request) {
		ChangePasswordReqeustDto dto = new ChangePasswordReqeustDto();
		BeanUtils.copyProperties(request, dto, null, "confirmationPassword");
		return userService.changeTmpPassword(dto);
	}

	@GetMapping("authentication")
	public AuthenticatedResponse getAuthentication(@AuthenticationPrincipal CustomUserDetails userDetails) {
		return Optional.ofNullable(userDetails)
				.map(u -> new AuthenticatedResponse(u.getUsername(), u.getAccessToken()))
				.orElseThrow(FailedAuthenticationException::new);
	}
}
