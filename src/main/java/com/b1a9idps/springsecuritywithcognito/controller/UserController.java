package com.b1a9idps.springsecuritywithcognito.controller;

import com.b1a9idps.springsecuritywithcognito.security.core.userdetails.CustomUserDetails;
import com.b1a9idps.springsecuritywithcognito.service.UserService;
import com.b1a9idps.springsecuritywithcognito.service.dto.response.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("{id}")
	public UserResponse get(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@PathVariable Integer id) {
		return userService.get(id);
	}
}
