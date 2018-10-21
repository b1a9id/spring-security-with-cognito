package com.example.springsecuritywithcognito.security.core.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomUserDetailsService;
import com.example.springsecuritywithcognito.utils.JWTUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

public class UserAccessTokenAuthenticationProvider implements AuthenticationProvider {

	private final CognitoProps cognitoProps;
	private CustomUserDetailsService userDetailsService;

	public UserAccessTokenAuthenticationProvider(CognitoProps cognitoProps) {
		this.cognitoProps = cognitoProps;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String accessToken = Optional.ofNullable(authentication.getCredentials())
				.map(Object::toString)
				.orElse(null);
		if (StringUtils.isEmpty(accessToken)) {
			throw new BadCredentialsException("access token not found.");
		}

		DecodedJWT decodedAccessToken = JWTUtils.decode(accessToken);
		if (decodedAccessToken == null) {
			throw new BadCredentialsException("access token invalid.");
		}

		if (invalidAccessToken(decodedAccessToken)) {
			throw new BadCredentialsException("access token invalid");
		}

		if (isKidInvalid(decodedAccessToken)) {
			throw new BadCredentialsException("kid invalid");
		}

		if (isIssuerInvalid(decodedAccessToken)) {
			throw new BadCredentialsException("issuer invalid");
		}

		String username = decodedAccessToken.getClaim("username").asString();
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		return new PreAuthenticatedAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
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

	/**
	 * ローカルのキーID(kid)とパブリックのkidの比較
	 * @param decodedAccessToken
	 * @return
	 */
	private boolean isKidInvalid(DecodedJWT decodedAccessToken) {
		return !nullSafeEquals(cognitoProps.getKid(), decodedAccessToken.getKeyId());
	}

	/**
	 * 発行者(iss)のクレームがユーザプールと一致する
	 * @param decodedAccessToken
	 * @return
	 */
	private boolean isIssuerInvalid(DecodedJWT decodedAccessToken) {
		return !nullSafeEquals(cognitoProps.getIssuer(), decodedAccessToken.getIssuer());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setUserDetailsService(CustomUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
