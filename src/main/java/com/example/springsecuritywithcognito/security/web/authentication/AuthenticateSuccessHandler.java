package com.example.springsecuritywithcognito.security.web.authentication;

import com.example.springsecuritywithcognito.security.core.userdetails.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.service.UserService;
import com.example.springsecuritywithcognito.utils.CookieUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticateSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final UserService userService;

	public AuthenticateSuccessHandler(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);
		setDefaultTargetUrl("/logout");

		if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
			super.onAuthenticationSuccess(request, response, authentication);
			return;
		}

		AuthenticatedUserDetails userDetails = (AuthenticatedUserDetails) authentication.getPrincipal();
		CookieUtils.addCookie(request, response, "access-token-name", userDetails.getAccessToken());

		userService.updateLastSignInAt(userDetails.getUsername());
		setDefaultTargetUrl("/users/" + userDetails.getUser().getId());
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
