package com.b1a9idps.springsecuritywithcognito.service.dto.request;

import java.io.Serializable;

public class ChangePasswordReqeustDto implements Serializable {
	private String username;

	private String password;

	private String session;

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

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
