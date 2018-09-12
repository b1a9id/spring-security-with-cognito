package com.example.springsecuritywithcognito.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.dto.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.utils.JWTUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	private final UserDetailsService userDetailsService;
	private final CognitoProps cognitoProps;

	public JWTAuthorizationFilter(
			AuthenticationManager authenticationManager,
			UserDetailsService userDetailsService,
			CognitoProps cognitoProps) {
		super(authenticationManager);
		this.userDetailsService = userDetailsService;
		this.cognitoProps = cognitoProps;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (!StringUtils.isEmpty(accessToken) && accessToken.startsWith("Bearer ")) {
			UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken.split(" ")[1]);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
		DecodedJWT decodedAccessToken = JWTUtils.decode(accessToken);
		if (decodedAccessToken == null) {
			return null;
		}

		try {
			if (invalidAccessToken(decodedAccessToken)) {
				return null;
			}

			String username = decodedAccessToken.getClaim("username").asString();
			User user = ((AuthenticatedUserDetails) userDetailsService.loadUserByUsername(username)).getUser();
			UserDetails userDetails = new AuthenticatedUserDetails(user, accessToken);
			return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		} catch (UsernameNotFoundException e) {
			return null;
		}
	}

	private boolean invalidAccessToken(DecodedJWT decodedAccessToken) {
		if (!ObjectUtils.nullSafeEquals(cognitoProps.getIssuer(), decodedAccessToken.getIssuer())) {
			return true;
		}
		if (isTokenExpired(decodedAccessToken)) {
			return true;
		}
		return false;
	}

	private boolean isTokenExpired(DecodedJWT decodedToken) {
		LocalDateTime expiredAt = decodedToken.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime now = LocalDateTime.now();
		return now.isAfter(expiredAt);
	}
}
