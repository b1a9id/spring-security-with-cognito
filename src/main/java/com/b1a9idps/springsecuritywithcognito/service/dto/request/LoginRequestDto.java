package com.b1a9idps.springsecuritywithcognito.service.dto.request;

import java.io.Serializable;

public class LoginRequestDto implements Serializable {
	private String username;

	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}