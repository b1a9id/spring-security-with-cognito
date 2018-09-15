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
	private String passwordConfirmation;

	@NotBlank
	private String session;

	@AssertTrue
	public boolean isEqualsPassword() {
		return ObjectUtils.nullSafeEquals(this.password, this.password);
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

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
}
