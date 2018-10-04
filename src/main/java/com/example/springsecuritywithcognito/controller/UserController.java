package com.example.springsecuritywithcognito.controller;

import com.example.springsecuritywithcognito.security.core.userdetails.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.service.dto.response.UserResponse;
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
			@AuthenticationPrincipal AuthenticatedUserDetails userDetails,
			@PathVariable Integer id) {
		return userService.get(id);
	}
}
