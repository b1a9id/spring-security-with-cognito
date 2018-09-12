package com.example.springsecuritywithcognito.service.dto.response;

import java.io.Serializable;

public class AuthenticatedResponse implements Serializable {
	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public AuthenticatedResponse(String accessToken) {
		this.accessToken = accessToken;
	}
}
