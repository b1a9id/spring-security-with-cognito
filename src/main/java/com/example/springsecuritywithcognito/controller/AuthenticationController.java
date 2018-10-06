package com.example.springsecuritywithcognito.controller;

import com.example.springsecuritywithcognito.controller.dto.request.ChangePasswordRequest;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.FirstLoginResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

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
}
