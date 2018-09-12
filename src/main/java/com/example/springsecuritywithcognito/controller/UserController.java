package com.example.springsecuritywithcognito.controller;

import com.example.springsecuritywithcognito.controller.dto.request.ChangePasswordRequest;
import com.example.springsecuritywithcognito.security.dto.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.service.dto.request.ChangePasswordReqeustDto;
import com.example.springsecuritywithcognito.service.dto.response.AuthenticatedResponse;
import com.example.springsecuritywithcognito.service.dto.response.UserResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("{username}")
	public UserResponse get(
			@AuthenticationPrincipal AuthenticatedUserDetails userDetails,
			@PathVariable String username) {
		return userService.getByUsername(username);
	}

	@PatchMapping("change-password")
	public AuthenticatedResponse changePassword(@RequestBody @Validated ChangePasswordRequest request) {
		ChangePasswordReqeustDto dto = new ChangePasswordReqeustDto();
		BeanUtils.copyProperties(request, dto, null, "confirmationPassword");
		return userService.changeTmpPassword(dto);
	}
}
