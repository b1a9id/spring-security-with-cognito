package com.example.springsecuritywithcognito.service.dto.response;

import java.io.Serializable;

public class AuthenticatedResponse implements Serializable {

	private String username;

	private String accessToken;

	public String getUsername() {
		return username;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public AuthenticatedResponse(String username, String accessToken) {
		this.username = username;
		this.accessToken = accessToken;
	}
}
