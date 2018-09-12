package com.example.springsecuritywithcognito.controller.dto.request;

import org.springframework.util.ObjectUtils;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ChangePasswordRequest implements Serializable {
	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@NotBlank
	private String confirmationPassword;

	@NotBlank
	private String session;

	@AssertTrue
	public boolean isEqulasPassword() {
		return ObjectUtils.nullSafeEquals(this.password, this.confirmationPassword);
	}

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

	public String getConfirmationPassword() {
		return confirmationPassword;
	}

	public void setConfirmationPassword(String confirmationPassword) {
		this.confirmationPassword = confirmationPassword;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
