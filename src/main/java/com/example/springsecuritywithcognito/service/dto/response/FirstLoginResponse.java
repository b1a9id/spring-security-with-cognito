package com.example.springsecuritywithcognito.service.dto.response;

import java.io.Serializable;

public class FirstLoginResponse implements Serializable {
	private String session;

	public FirstLoginResponse(String session) {
		this.session = session;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
