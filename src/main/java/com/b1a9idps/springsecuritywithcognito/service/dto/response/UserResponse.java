package com.b1a9idps.springsecuritywithcognito.service.dto.response;

import com.b1a9idps.springsecuritywithcognito.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UserResponse implements Serializable {
	private String name;

	private String username;

	private LocalDateTime createdAt;

	private LocalDateTime lastSignInAt;

	private UserResponse(User user) {
		this.name = user.getName();
		this.username = user.getUsername();
		this.createdAt = user.getCreatedAt();
		this.lastSignInAt = user.getLastSignInAt();
	}

	public static UserResponse of(User user) {
		return new UserResponse(user);
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getLastSignInAt() {
		return lastSignInAt;
	}
}
