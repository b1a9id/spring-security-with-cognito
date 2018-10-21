package com.example.springsecuritywithcognito.service.dto.response;

import java.io.Serializable;

public class AuthenticatedResponse implements Serializable {

	private String usename;

	private String accessToken;

	public String getUsename() {
		return usename;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public AuthenticatedResponse(String username, String accessToken) {
		this.usename = username;
		this.accessToken = accessToken;
	}
}
